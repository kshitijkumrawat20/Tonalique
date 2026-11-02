import { useState } from 'react';
import {
  ActivityIndicator,
  Image,
  Linking,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { LinearGradient } from 'expo-linear-gradient';
import { StatusBar } from 'expo-status-bar';
import { tonaliqueApi } from './src/api';
import type { AnalysisResult, Preferences, Product, RecommendationResult, TryOnResult } from './src/types';

type Screen = 'welcome' | 'preferences' | 'consent' | 'photo' | 'analysis' | 'insights' | 'recommendations' | 'tryon';

const initialPreferences: Preferences = {
  occasion: 'Everyday',
  look: 'Soft glam',
  finish: 'Satin',
  shadePreference: 'Not sure',
  budgetMinor: 150000,
};

const money = (minor: number) => `₹${Math.round(minor / 100).toLocaleString('en-IN')}`;

export default function App() {
  const [screen, setScreen] = useState<Screen>('welcome');
  const [preferences, setPreferences] = useState(initialPreferences);
  const [sessionId, setSessionId] = useState('');
  const [mediaId, setMediaId] = useState('');
  const [photoUri, setPhotoUri] = useState('');
  const [analysis, setAnalysis] = useState<AnalysisResult>();
  const [recommendations, setRecommendations] = useState<RecommendationResult>();
  const [selectedProduct, setSelectedProduct] = useState<Product>();
  const [tryOn, setTryOn] = useState<TryOnResult>();
  const [error, setError] = useState('');
  const [starting, setStarting] = useState(false);

  const startSession = async () => {
    if (starting) return;
    setError('');
    setStarting(true);
    try {
      const session = await tonaliqueApi.createSession();
      setSessionId(session.session_id);
      setScreen('preferences');
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Tonalique is unavailable right now.');
    } finally {
      setStarting(false);
    }
  };

  const choosePhoto = async () => {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ['images'],
      allowsEditing: true,
      aspect: [3, 4],
      quality: 0.88,
    });
    if (!result.canceled) {
      setPhotoUri(result.assets[0].uri);
      setScreen('photo');
    }
  };

  const submitPhoto = async () => {
    if (!photoUri) return;
    setError('');
    setScreen('analysis');
    try {
      const media = await tonaliqueApi.uploadPhoto(sessionId, photoUri);
      setMediaId(media.media_id);
      const result = await tonaliqueApi.runAnalysis(sessionId, media.media_id);
      setAnalysis(result);
      setScreen('insights');
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'We could not process that photo.');
      setScreen('photo');
    }
  };

  const loadRecommendations = async () => {
    setError('');
    try {
      setRecommendations(await tonaliqueApi.getRecommendations(preferences));
      setScreen('recommendations');
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Recommendations are unavailable.');
    }
  };

  const startTryOn = async (product: Product) => {
    setSelectedProduct(product);
    setError('');
    setTryOn(undefined);
    setScreen('tryon');
    try {
      setTryOn(await tonaliqueApi.runTryOn(sessionId, mediaId, product.id));
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'The virtual preview is unavailable.');
    }
  };

  const updatePreference = (key: keyof Preferences, value: string | number) => {
    setPreferences((current) => ({ ...current, [key]: value }));
  };

  return (
    <View style={styles.app}>
      <StatusBar style="dark" />
      {screen === 'welcome' && <Welcome error={error} starting={starting} onStart={startSession} />}
      {screen === 'preferences' && (
        <PreferencesScreen
          preferences={preferences}
          onUpdate={updatePreference}
          onContinue={() => setScreen('consent')}
        />
      )}
      {screen === 'consent' && <Consent onAgree={() => setScreen('photo')} onCancel={() => setScreen('preferences')} />}
      {screen === 'photo' && (
        <PhotoScreen
          photoUri={photoUri}
          error={error}
          onChoose={choosePhoto}
          onUse={submitPhoto}
          onBack={() => setScreen('consent')}
        />
      )}
      {screen === 'analysis' && <Progress title="Building your skin snapshot" detail="Running cosmetic analysis. Keep the app open while we finish." />}
      {screen === 'insights' && analysis && (
        <InsightsScreen analysis={analysis} onContinue={loadRecommendations} onBack={() => setScreen('preferences')} />
      )}
      {screen === 'recommendations' && recommendations && (
        <RecommendationsScreen
          result={recommendations}
          error={error}
          onChoose={startTryOn}
          onBack={() => setScreen('insights')}
        />
      )}
      {screen === 'tryon' && selectedProduct && (
        <TryOnScreen
          product={selectedProduct}
          photoUri={photoUri}
          result={tryOn}
          error={error}
          onBack={() => setScreen('recommendations')}
        />
      )}
    </View>
  );
}

function Page({ children, onBack }: { children: React.ReactNode; onBack?: () => void }) {
  return (
    <ScrollView contentContainerStyle={styles.page} showsVerticalScrollIndicator={false}>
      {onBack && <Pressable onPress={onBack} style={styles.back}><Text style={styles.backText}>‹  Back</Text></Pressable>}
      {children}
    </ScrollView>
  );
}

function Welcome({ error, starting, onStart }: { error: string; starting: boolean; onStart: () => void }) {
  return (
    <Page>
      <Text style={styles.eyebrow}>TONALIQUE · PERSONAL BEAUTY STUDIO</Text>
      <LinearGradient colors={['#EED9D3', '#D6B0A9']} style={styles.hero}>
        <View style={styles.heroOrb} />
        <Text style={styles.heroLabel}>YOUR LOOK,{"\n"}YOUR TONE</Text>
      </LinearGradient>
      <Text style={styles.display}>A look that feels like yours.</Text>
      <Text style={styles.body}>Explore cosmetic insights, find a look within your budget, and preview it before you decide.</Text>
      {error ? <Text style={styles.error}>{error}</Text> : null}
      <Pressable style={[styles.primaryButton, starting && styles.primaryButtonDisabled]} onPress={onStart} disabled={starting}><Text style={styles.primaryText}>{starting ? 'Connecting…' : 'Create my look'}</Text></Pressable>
      <Text style={styles.helper}>No account needed.</Text>
      <Text style={styles.privacy}>Your photo is optional and is only processed after your explicit consent.</Text>
    </Page>
  );
}

function PreferencesScreen({ preferences, onUpdate, onContinue }: { preferences: Preferences; onUpdate: (key: keyof Preferences, value: string | number) => void; onContinue: () => void }) {
  const choices = (title: string, key: keyof Preferences, values: string[]) => (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>{title}</Text>
      <View style={styles.chips}>{values.map((value) => <Pressable key={value} onPress={() => onUpdate(key, value)} style={[styles.chip, preferences[key] === value && styles.chipSelected]}><Text style={[styles.chipText, preferences[key] === value && styles.chipTextSelected]}>{value}</Text></Pressable>)}</View>
    </View>
  );
  return <Page onBack={() => undefined}>
    <Text style={styles.eyebrow}>LET'S SET THE DIRECTION</Text>
    <Text style={styles.title}>Build your brief.</Text>
    <Text style={styles.body}>You can change these choices anytime. They guide the shortlist, not your identity.</Text>
    {choices('Occasion', 'occasion', ['Everyday', 'Work', 'Dinner', 'Celebration'])}
    {choices('Look', 'look', ['Natural', 'Soft glam', 'Bold', 'Minimal'])}
    {choices('Finish', 'finish', ['Matte', 'Satin', 'Dewy', 'No preference'])}
    {choices('Shade family', 'shadePreference', ['Warm', 'Cool', 'Neutral', 'Not sure'])}
    <View style={styles.section}><Text style={styles.sectionTitle}>Maximum spend</Text><View style={styles.budgetRow}>{[75000, 150000, 250000].map((value) => <Pressable key={value} onPress={() => onUpdate('budgetMinor', value)} style={[styles.budget, preferences.budgetMinor === value && styles.budgetSelected]}><Text style={[styles.budgetText, preferences.budgetMinor === value && styles.chipTextSelected]}>{money(value)}</Text></Pressable>)}</View><TextInput value={String(preferences.budgetMinor / 100)} onChangeText={(value) => onUpdate('budgetMinor', Math.max(0, Number(value.replace(/\D/g, '')) * 100))} keyboardType="number-pad" placeholder="Custom budget in ₹" style={styles.input} /></View>
    <Pressable style={styles.primaryButton} onPress={onContinue}><Text style={styles.primaryText}>Continue</Text></Pressable>
  </Page>;
}

function Consent({ onAgree, onCancel }: { onAgree: () => void; onCancel: () => void }) {
  return <Page onBack={onCancel}><Text style={styles.eyebrow}>PHOTO CONSENT</Text><Text style={styles.title}>Your photo, your choice.</Text><Text style={styles.body}>The photo you choose is sent to Tonalique's backend and then to YouCam for the requested cosmetic analysis and preview.</Text><View style={styles.notice}><Text style={styles.noticeTitle}>Before you continue</Text><Text style={styles.body}>Tonalique keeps the source image temporarily for this session and does not use it for identity recognition. You can cancel without uploading.</Text></View><Text style={styles.privacy}>AI cosmetic reference only. This is not a medical diagnosis and does not replace advice from a qualified professional.</Text><Pressable style={styles.primaryButton} onPress={onAgree}><Text style={styles.primaryText}>I agree and choose a photo</Text></Pressable><Pressable onPress={() => Linking.openURL('https://example.com/tonalique-privacy')}><Text style={styles.link}>Read the full privacy notice</Text></Pressable></Page>;
}

function PhotoScreen({ photoUri, error, onChoose, onUse, onBack }: { photoUri: string; error: string; onChoose: () => void; onUse: () => void; onBack: () => void }) {
  return <Page onBack={onBack}><Text style={styles.eyebrow}>PHOTO REVIEW</Text><Text style={styles.title}>Choose a clear, front-facing photo.</Text><Text style={styles.body}>Even lighting, visible face and no strong filters help the provider return a useful result.</Text>{photoUri ? <Image source={{ uri: photoUri }} style={styles.photo} /> : <View style={styles.photoPlaceholder}><Text style={styles.photoIcon}>＋</Text><Text style={styles.body}>Your selected photo will appear here.</Text></View>}{error ? <Text style={styles.error}>{error}</Text> : null}<Pressable style={styles.primaryButton} onPress={photoUri ? onUse : onChoose}><Text style={styles.primaryText}>{photoUri ? 'Use this photo' : 'Choose from Photo Library'}</Text></Pressable>{photoUri && <Pressable style={styles.secondaryButton} onPress={onChoose}><Text style={styles.secondaryText}>Choose another</Text></Pressable>}</Page>;
}

function Progress({ title, detail }: { title: string; detail: string }) {
  return <View style={styles.center}><ActivityIndicator size="large" color="#704052" /><Text style={styles.titleCenter}>{title}</Text><Text style={styles.bodyCenter}>{detail}</Text><Text style={styles.progressStep}>Preparing · Processing · Organising</Text></View>;
}

function InsightsScreen({ analysis, onContinue, onBack }: { analysis: AnalysisResult; onContinue: () => void; onBack: () => void }) {
  return <Page onBack={onBack}><Text style={styles.eyebrow}>COSMETIC REFERENCE</Text><Text style={styles.title}>Your skin snapshot.</Text><Text style={styles.body}>{analysis.note}</Text><View style={styles.metricGrid}>{analysis.insights.map((insight) => <View key={insight.id} style={styles.metric}><Text style={styles.metricLabel}>{insight.title.toUpperCase()}</Text><Text style={styles.metricValue}>{insight.value}</Text><Text style={styles.metricNote}>{insight.note}</Text></View>)}</View><View style={styles.notice}><Text style={styles.noticeTitle}>A considered next step</Text><Text style={styles.body}>We'll use your brief and a verified catalogue to build a small, budget-aware shortlist.</Text></View><Pressable style={styles.primaryButton} onPress={onContinue}><Text style={styles.primaryText}>See my shortlist</Text></Pressable></Page>;
}

function RecommendationsScreen({ result, error, onChoose, onBack }: { result: RecommendationResult; error: string; onChoose: (product: Product) => void; onBack: () => void }) {
  return <Page onBack={onBack}><Text style={styles.eyebrow}>CURATED CATALOGUE</Text><Text style={styles.title}>A look within reach.</Text><View style={styles.totalCard}><View><Text style={styles.metricLabel}>ESTIMATED TOTAL</Text><Text style={styles.total}>{money(result.totalMinor)}</Text></View><Text style={result.remainingMinor >= 0 ? styles.within : styles.over}>{result.remainingMinor >= 0 ? `${money(result.remainingMinor)} left` : 'Over budget'}</Text></View>{result.items.map((product) => <ProductCard key={product.id} product={product} onChoose={onChoose} />)}<Text style={styles.privacy}>Prices and availability come from the verified catalogue. Shipping and tax are excluded unless stated.</Text>{error ? <Text style={styles.error}>{error}</Text> : null}</Page>;
}

function ProductCard({ product, onChoose }: { product: Product; onChoose: (product: Product) => void }) {
  return <View style={styles.productCard}><View style={[styles.swatch, { backgroundColor: product.swatch }]} /><View style={styles.productInfo}><Text style={styles.productBrand}>{product.brand.toUpperCase()}</Text><Text style={styles.productName}>{product.name}</Text><Text style={styles.productMeta}>{product.shade} · {product.finish}</Text><Text style={styles.reason}>{product.reason}</Text><View style={styles.productBottom}><Text style={styles.price}>{money(product.priceMinor)}</Text><Pressable onPress={() => onChoose(product)} style={styles.smallButton}><Text style={styles.smallButtonText}>Preview</Text></Pressable></View></View></View>;
}

function TryOnScreen({ product, photoUri, result, error, onBack }: { product: Product; photoUri: string; result?: TryOnResult; error: string; onBack: () => void }) {
  return <Page onBack={onBack}><Text style={styles.eyebrow}>VIRTUAL TRY-ON</Text><Text style={styles.title}>Your preview is ready.</Text>{result ? <View style={styles.previewFrame}>{result.previewUrl ? <Image source={{ uri: result.previewUrl }} style={styles.previewImage} /> : photoUri ? <Image source={{ uri: photoUri }} style={styles.previewImage} /> : <LinearGradient colors={['#EED9D3', '#B87A77']} style={styles.previewImage}><Text style={styles.previewLabel}>AI PREVIEW</Text></LinearGradient>}<View style={styles.previewBadge}><Text style={styles.previewBadgeText}>AI PREVIEW</Text></View></View> : <View style={styles.loadingCard}><ActivityIndicator color="#704052" /><Text style={styles.body}>Applying {product.shade}…</Text></View>}<View style={styles.lookCard}><Text style={styles.metricLabel}>SELECTED PRODUCT</Text><Text style={styles.productName}>{product.name}</Text><Text style={styles.productMeta}>{product.brand} · {product.shade} · {money(product.priceMinor)}</Text></View>{result && <Text style={styles.privacy}>{result.disclaimer}</Text>}{error ? <Text style={styles.error}>{error}</Text> : null}<Pressable style={styles.secondaryButton} onPress={onBack}><Text style={styles.secondaryText}>Back to shortlist</Text></Pressable></Page>;
}

const styles = StyleSheet.create({
  app: { flex: 1, backgroundColor: '#F7F3EF' },
  page: { padding: 22, paddingTop: 58, paddingBottom: 42, gap: 16 },
  back: { minHeight: 48, justifyContent: 'center', alignSelf: 'flex-start' },
  backText: { color: '#704052', fontSize: 16, fontWeight: '700' },
  eyebrow: { color: '#70666A', fontSize: 11, letterSpacing: 1.5, fontWeight: '700' },
  display: { color: '#292326', fontSize: 38, lineHeight: 44, fontWeight: '400', marginTop: 8 },
  title: { color: '#292326', fontSize: 32, lineHeight: 38, fontWeight: '700' },
  body: { color: '#70666A', fontSize: 16, lineHeight: 24 },
  helper: { color: '#704052', textAlign: 'center', fontWeight: '700' },
  privacy: { color: '#70666A', fontSize: 13, lineHeight: 19 },
  hero: { height: 245, borderRadius: 28, overflow: 'hidden', justifyContent: 'flex-end', padding: 22, marginTop: 4 },
  heroOrb: { position: 'absolute', width: 220, height: 220, borderRadius: 110, backgroundColor: '#F7F3EF', opacity: 0.65, right: -25, top: -25 },
  heroLabel: { color: '#704052', fontSize: 14, letterSpacing: 2, fontWeight: '800', lineHeight: 22 },
  primaryButton: { backgroundColor: '#704052', minHeight: 54, borderRadius: 28, alignItems: 'center', justifyContent: 'center', paddingHorizontal: 24, marginTop: 8 },
  primaryButtonDisabled: { opacity: 0.6 },
  primaryText: { color: '#FFFCF9', fontSize: 16, fontWeight: '700' },
  secondaryButton: { minHeight: 52, borderRadius: 27, borderWidth: 1, borderColor: '#E4DAD4', alignItems: 'center', justifyContent: 'center', paddingHorizontal: 20 },
  secondaryText: { color: '#292326', fontSize: 16, fontWeight: '700' },
  link: { color: '#704052', textAlign: 'center', fontWeight: '700', padding: 12 },
  section: { gap: 10, marginTop: 4 },
  sectionTitle: { color: '#292326', fontSize: 18, fontWeight: '700' },
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  chip: { borderWidth: 1, borderColor: '#E4DAD4', backgroundColor: '#FFFCF9', borderRadius: 22, paddingHorizontal: 15, paddingVertical: 11 },
  chipSelected: { backgroundColor: '#F1DEDA', borderColor: '#704052' },
  chipText: { color: '#70666A', fontSize: 14, fontWeight: '600' },
  chipTextSelected: { color: '#704052' },
  budgetRow: { flexDirection: 'row', gap: 8 },
  budget: { flex: 1, backgroundColor: '#FFFCF9', borderWidth: 1, borderColor: '#E4DAD4', borderRadius: 16, alignItems: 'center', paddingVertical: 13 },
  budgetSelected: { backgroundColor: '#F1DEDA', borderColor: '#704052' },
  budgetText: { color: '#70666A', fontWeight: '700' },
  input: { backgroundColor: '#FFFCF9', borderWidth: 1, borderColor: '#E4DAD4', borderRadius: 14, padding: 15, color: '#292326', fontSize: 16 },
  notice: { backgroundColor: '#F1DEDA', borderWidth: 1, borderColor: '#D9A9A7', borderRadius: 18, padding: 16, gap: 7 },
  noticeTitle: { color: '#704052', fontSize: 17, fontWeight: '700' },
  photo: { width: '100%', aspectRatio: 3 / 4, borderRadius: 24, backgroundColor: '#EEE6E0' },
  photoPlaceholder: { height: 330, borderRadius: 24, borderWidth: 1, borderColor: '#E4DAD4', borderStyle: 'dashed', backgroundColor: '#FFFCF9', justifyContent: 'center', alignItems: 'center', padding: 30, gap: 10 },
  photoIcon: { color: '#704052', fontSize: 42 },
  error: { color: '#A33C42', fontSize: 14, lineHeight: 20 },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 32, gap: 18 },
  titleCenter: { color: '#292326', fontSize: 25, fontWeight: '700', textAlign: 'center' },
  bodyCenter: { color: '#70666A', fontSize: 16, lineHeight: 24, textAlign: 'center' },
  progressStep: { color: '#704052', fontSize: 12, letterSpacing: 1, fontWeight: '700' },
  metricGrid: { flexDirection: 'row', flexWrap: 'wrap', gap: 12 },
  metric: { backgroundColor: '#FFFCF9', borderWidth: 1, borderColor: '#E4DAD4', borderRadius: 20, padding: 15, width: '47%', minHeight: 135, gap: 6 },
  metricLabel: { color: '#70666A', fontSize: 11, letterSpacing: 1, fontWeight: '700' },
  metricValue: { color: '#292326', fontSize: 21, fontWeight: '700' },
  metricNote: { color: '#70666A', fontSize: 12, lineHeight: 17 },
  totalCard: { backgroundColor: '#FFFCF9', borderWidth: 1, borderColor: '#E4DAD4', borderRadius: 20, padding: 18, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  total: { color: '#292326', fontSize: 27, fontWeight: '800', marginTop: 4 },
  within: { color: '#596B55', fontWeight: '700' },
  over: { color: '#A33C42', fontWeight: '700' },
  productCard: { backgroundColor: '#FFFCF9', borderWidth: 1, borderColor: '#E4DAD4', borderRadius: 20, padding: 14, flexDirection: 'row', gap: 14 },
  swatch: { width: 78, height: 118, borderRadius: 15 },
  productInfo: { flex: 1, gap: 4 },
  productBrand: { color: '#70666A', fontSize: 10, letterSpacing: 1.1, fontWeight: '700' },
  productName: { color: '#292326', fontSize: 18, fontWeight: '700' },
  productMeta: { color: '#704052', fontSize: 14 },
  reason: { color: '#70666A', fontSize: 13, lineHeight: 18, marginTop: 3 },
  productBottom: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginTop: 7 },
  price: { color: '#292326', fontSize: 19, fontWeight: '800' },
  smallButton: { backgroundColor: '#704052', borderRadius: 18, paddingHorizontal: 14, paddingVertical: 9 },
  smallButtonText: { color: '#FFFCF9', fontWeight: '700', fontSize: 13 },
  previewFrame: { width: '100%', aspectRatio: 3 / 4, borderRadius: 24, overflow: 'hidden', position: 'relative', backgroundColor: '#EEE6E0' },
  previewImage: { width: '100%', height: '100%', justifyContent: 'flex-end', alignItems: 'center', paddingBottom: 22 },
  previewLabel: { color: '#FFFCF9', letterSpacing: 2, fontWeight: '800' },
  previewBadge: { position: 'absolute', top: 14, left: 14, borderRadius: 14, backgroundColor: '#292326CC', paddingHorizontal: 10, paddingVertical: 7 },
  previewBadgeText: { color: '#FFFCF9', fontSize: 10, letterSpacing: 1, fontWeight: '800' },
  loadingCard: { height: 300, backgroundColor: '#FFFCF9', borderRadius: 24, justifyContent: 'center', alignItems: 'center', gap: 14 },
  lookCard: { backgroundColor: '#FFFCF9', borderWidth: 1, borderColor: '#E4DAD4', borderRadius: 20, padding: 16, gap: 7 },
});
