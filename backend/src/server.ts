import cors from 'cors';
import dotenv from 'dotenv';
import express from 'express';
import multer from 'multer';
import crypto from 'node:crypto';

dotenv.config({ path: process.env.TONALIQUE_ENV_FILE ?? '../.env' });

type Product = {
  id: string;
  brand: string;
  name: string;
  shade: string;
  finish: string;
  category: string;
  priceMinor: number;
  reason: string;
  sourceUrl: string;
  swatch: string;
};

type Session = { id: string; createdAt: number; media?: { bytes: Buffer; type: string } };
type Task = { id: string; kind: 'skin_analysis' | 'try_on'; status: string; result?: unknown; error?: string; providerTaskId?: string };
type ProviderEnvelope<T> = { status?: number; data?: T; task_id?: string; task_status?: string; task_status_msg?: string; error?: unknown; error_code?: string | number; error_message?: string; result?: unknown; result_url?: string; preview_url?: string; image_url?: string };

const app = express();
const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: 8 * 1024 * 1024, files: 1 },
  fileFilter: (_request, file, callback) => callback(null, ['image/jpeg', 'image/png', 'image/webp'].includes(file.mimetype)),
});
const sessions = new Map<string, Session>();
const tasks = new Map<string, Task>();
const products: Product[] = [
  { id: 'sku-rosewood', brand: 'Atelier Edit', name: 'Satin Veil Lip Colour', shade: 'Rosewood', finish: 'Satin', category: 'Lips', priceMinor: 79900, reason: 'Matches your satin finish preference', sourceUrl: 'https://example.com/tonalique-demo', swatch: '#8E4D57' },
  { id: 'sku-terracotta', brand: 'Atelier Edit', name: 'Soft Focus Cheek Tint', shade: 'Terracotta', finish: 'Natural', category: 'Cheeks', priceMinor: 62000, reason: 'Adds a warm, buildable colour wash', sourceUrl: 'https://example.com/tonalique-demo', swatch: '#B86F59' },
  { id: 'sku-satin-base', brand: 'Atelier Edit', name: 'Second Skin Tint', shade: 'Neutral 03', finish: 'Satin', category: 'Base', priceMinor: 45000, reason: 'Keeps the look light and even', sourceUrl: 'https://example.com/tonalique-demo', swatch: '#C99478' },
];

const mode = process.env.TONALIQUE_MODE ?? 'live';
const providerBaseUrl = (process.env.YOUCAM_API_BASE_URL ?? 'https://yce-api-01.makeupar.com').trim().replace(/\/$/, '');
const providerAuthScheme = process.env.YOUCAM_AUTH_SCHEME ?? 'Bearer';
const json = express.json({ limit: '100kb' });
app.use(cors({ origin: process.env.CORS_ORIGIN ?? '*' }));
app.use(json);

const id = (prefix: string) => `${prefix}_${crypto.randomBytes(12).toString('hex')}`;
const getSession = (sessionId: string) => {
  const session = sessions.get(sessionId);
  if (!session || Date.now() - session.createdAt > 30 * 60 * 1000) throw new Error('Session expired');
  return session;
};

const providerRequest = async <T>(path: string, init: RequestInit): Promise<T> => {
  const apiKey = process.env.YOUCAM_API_KEY?.trim();
  if (!apiKey) throw new Error('YOUCAM_API_KEY is not configured');
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 20000);
  let response: Response;
  try {
    response = await fetch(`${providerBaseUrl}${path}`, {
      ...init,
      signal: controller.signal,
      headers: {
        Authorization: `${providerAuthScheme} ${apiKey}`,
        'Content-Type': 'application/json',
        ...init.headers,
      },
    });
  } catch (error) {
    if (error instanceof Error && error.name === 'AbortError') {
      throw new Error(`YouCam request timed out: ${path}`);
    }
    throw error;
  } finally {
    clearTimeout(timeout);
  }
  const body = await response.text();
  if (!response.ok) {
    let detail = body;
    try {
      const parsed = JSON.parse(body) as Record<string, unknown>;
      detail = String(parsed.message ?? parsed.error_message ?? parsed.error ?? parsed.data ?? body);
    } catch {
      // Preserve the provider's plain-text response when it is not JSON.
    }
    throw new Error(`YouCam request failed (${response.status}): ${detail.slice(0, 500)}`);
  }
  try {
    return JSON.parse(body) as T;
  } catch {
    throw new Error(`YouCam returned an invalid JSON response for ${path}`);
  }
};

const providerData = <T>(response: ProviderEnvelope<T>): T => response.data ?? (response as unknown as T);
const terminalSuccessStatuses = new Set(['success', 'succeeded', 'completed', 'complete']);
const terminalErrorStatuses = new Set(['error', 'failed', 'failure']);
const providerStatus = (value: Record<string, unknown>) => String(value.task_status ?? value.status ?? 'processing').toLowerCase();
const providerFailure = (value: Record<string, unknown>) => {
  const detail = value.error_message ?? value.task_status_msg ?? value.error_code ?? value.error;
  if (typeof detail === 'string' || typeof detail === 'number') return `provider_${detail}`;
  if (detail && typeof detail === 'object') {
    const objectDetail = detail as Record<string, unknown>;
    const message = objectDetail.message ?? objectDetail.msg ?? objectDetail.error_message ?? objectDetail.code;
    if (typeof message === 'string' || typeof message === 'number') return `provider_${message}`;
  }
  return 'provider_task_failed';
};

const providerError = (error: unknown) => ({
  error: 'provider_request_failed',
  detail: error instanceof Error ? error.message : 'YouCam provider request failed',
});

const publicMediaUrl = (sessionId: string) => {
  const baseUrl = process.env.TONALIQUE_PUBLIC_MEDIA_BASE_URL?.trim().replace(/\/$/, '');
  if (!baseUrl) {
    throw new Error(
      'TONALIQUE_PUBLIC_MEDIA_BASE_URL is required in live mode. Start an HTTPS tunnel to this backend and set it to that public URL.',
    );
  }
  return `${baseUrl}/api/v1/media/${encodeURIComponent(sessionId)}`;
};

app.get('/health', (_request, response) => response.json({ ok: true, mode }));

app.post('/api/v1/sessions', (_request, response) => {
  const sessionId = id('sess');
  sessions.set(sessionId, { id: sessionId, createdAt: Date.now() });
  response.status(201).json({ session_id: sessionId, expires_at: new Date(Date.now() + 30 * 60 * 1000).toISOString() });
});

app.post('/api/v1/media', upload.single('file'), (request, response) => {
  try {
    const sessionId = String(request.body.session_id ?? '');
    const session = getSession(sessionId);
    if (!request.file) return response.status(400).json({ error: 'image_required' });
    session.media = { bytes: request.file.buffer, type: request.file.mimetype };
    response.status(201).json({ media_id: `${sessionId}_media`, status: 'ready' });
  } catch (error) {
    response.status(400).json({ error: error instanceof Error ? error.message : 'media_upload_failed' });
  }
});

app.get('/api/v1/media/:sessionId', (request, response) => {
  const session = sessions.get(request.params.sessionId);
  if (!session?.media || Date.now() - session.createdAt > 30 * 60 * 1000) return response.status(404).end();
  response.type(session.media.type).send(session.media.bytes);
});

app.post('/api/v1/skin-analyses', async (request, response) => {
  try {
    const { session_id: sessionId, media_id: mediaId } = request.body as { session_id?: string; media_id?: string };
    const session = getSession(String(sessionId));
    if (!mediaId || !session.media) return response.status(400).json({ error: 'media_not_ready' });
    const providerResponse = await providerRequest<ProviderEnvelope<{ task_id: string }>>('/s2s/v2.1/task/skin-analysis', {
      method: 'POST',
      body: JSON.stringify({
        src_file_url: publicMediaUrl(String(sessionId)),
        dst_actions: ['skin_type', 'radiance', 'redness', 'oiliness'],
        format: 'json',
      }),
    });
    const provider = providerData(providerResponse);
    const taskId = id('analysis');
    tasks.set(taskId, { id: taskId, kind: 'skin_analysis', status: 'processing', providerTaskId: provider.task_id });
    return response.status(202).json({ analysis_id: taskId, status: 'queued' });
  } catch (error) {
    return response.status(502).json(providerError(error));
  }
});

app.get('/api/v1/tasks/:taskId', async (request, response) => {
  const task = tasks.get(request.params.taskId);
  if (!task) return response.status(404).json({ error: 'task_not_found' });
  if (mode === 'live' && task.providerTaskId) {
    try {
      const providerResponse = await providerRequest<ProviderEnvelope<Record<string, unknown>>>(`/s2s/v2.1/task/skin-analysis/${encodeURIComponent(task.providerTaskId)}`, { method: 'GET' });
        const providerResult = providerData(providerResponse);
        const status = providerStatus(providerResult);
        task.status = terminalSuccessStatuses.has(status) ? 'success' : terminalErrorStatuses.has(status) ? 'error' : 'processing';
        task.result = task.status === 'success' ? normalizeAnalysis(providerResult) : undefined;
        task.error = task.status === 'error' ? providerFailure(providerResult) : undefined;
    } catch (error) {
      task.status = 'error';
      task.error = error instanceof Error ? error.message : 'provider_task_unavailable';
    }
  }
  return response.json({ task_id: task.id, kind: task.kind, status: task.status, result: task.result ?? null, error: task.error ?? null });
});

app.post('/api/v1/recommendations', (request, response) => {
  const budgetMinor = Number(request.body.budget_minor);
  if (!Number.isInteger(budgetMinor) || budgetMinor < 0) return response.status(400).json({ error: 'invalid_budget' });
  const items = products.filter((product) => product.priceMinor <= budgetMinor);
  const selected = items.length ? items : [products[0]];
  const totalMinor = selected.reduce((total, product) => total + product.priceMinor, 0);
  response.json({ recommendation_id: id('rec'), currency: 'INR', budget_minor: budgetMinor, total_minor: totalMinor, remaining_minor: budgetMinor - totalMinor, within_budget: totalMinor <= budgetMinor, items: selected });
});

app.post('/api/v1/try-ons', async (request, response) => {
  try {
    const { session_id: sessionId, media_id: mediaId, product_id: productId } = request.body as { session_id?: string; media_id?: string; product_id?: string };
    const session = getSession(String(sessionId));
    const product = products.find((candidate) => candidate.id === productId);
    if (!mediaId || !session.media || !product) return response.status(400).json({ error: 'invalid_try_on_request' });
    const providerResponse = await providerRequest<ProviderEnvelope<{ task_id: string }>>('/s2s/v2.0/task/makeup-vto', {
      method: 'POST',
      body: JSON.stringify({
        src_file_url: publicMediaUrl(String(sessionId)),
        version: '1.0',
        effects: [tryOnEffectFor(product)],
      }),
    });
    const provider = providerData(providerResponse);
    const taskId = id('tryon');
    tasks.set(taskId, { id: taskId, kind: 'try_on', status: 'processing', providerTaskId: provider.task_id });
    return response.status(202).json({ tryon_id: taskId, status: 'queued' });
  } catch (error) {
    return response.status(502).json(providerError(error));
  }
});

app.get('/api/v1/try-ons/:taskId', async (request, response) => {
  const task = tasks.get(request.params.taskId);
  if (!task || task.kind !== 'try_on') return response.status(404).json({ error: 'try_on_not_found' });
  if (mode === 'live' && task.providerTaskId) {
    try {
      const providerResponse = await providerRequest<ProviderEnvelope<Record<string, unknown>>>(`/s2s/v2.0/task/makeup-vto/${encodeURIComponent(task.providerTaskId)}`, { method: 'GET' });
        const providerResult = providerData(providerResponse);
        const status = providerStatus(providerResult);
        task.status = terminalSuccessStatuses.has(status) ? 'success' : terminalErrorStatuses.has(status) ? 'error' : 'processing';
        if (task.status === 'success') task.result = { preview_url: findPreviewUrl(providerResult), disclaimer: 'AI preview only; actual product appearance may vary.' };
        task.error = task.status === 'error' ? providerFailure(providerResult) : undefined;
    } catch (error) {
      task.status = 'error';
      task.error = error instanceof Error ? error.message : 'provider_task_unavailable';
    }
  }
  const result = task.result as { preview_url: string; disclaimer: string } | undefined;
  return response.json({ tryon_id: task.id, status: task.status, preview_url: result?.preview_url ?? '', disclaimer: result?.disclaimer ?? '', error: task.error ?? null });
});

app.delete('/api/v1/sessions/:sessionId', (request, response) => {
  sessions.delete(request.params.sessionId);
  return response.status(204).send();
});

const normalizeAnalysis = (providerResult: Record<string, unknown>) => {
  const values = (providerResult.result ?? providerResult.results ?? providerResult) as Record<string, unknown>;
  const fields = ['radiance', 'texture', 'oiliness', 'redness'];
  return {
    insights: fields.filter((field) => values[field] !== undefined).map((field) => ({
      id: field,
      title: field[0].toUpperCase() + field.slice(1),
      value: String(values[field]),
      note: 'A provider-reported cosmetic reference signal.',
    })),
    note: 'AI cosmetic reference only. This result is not a medical diagnosis and does not replace professional advice.',
  };
};

const findPreviewUrl = (providerResult: Record<string, unknown>): string => {
  const candidates = [providerResult.result_url, providerResult.preview_url, providerResult.image_url, (providerResult.result as Record<string, unknown> | undefined)?.result_url];
  return candidates.find((candidate): candidate is string => typeof candidate === 'string') ?? '';
};

const tryOnEffectFor = (product: Product): Record<string, unknown> => {
  switch (product.category) {
    case 'Cheeks':
      return {
        category: 'blush',
        pattern: { name: '2colors1' },
        palettes: [
          { color: product.swatch, texture: 'matte', colorIntensity: 55 },
          { color: '#D99883', texture: 'satin', glowStrength: 25, colorIntensity: 35 },
        ],
      };
    case 'Base':
      return {
        category: 'foundation',
        palettes: [{ color: product.swatch, colorIntensity: 45, glowIntensity: 35, coverageIntensity: 40 }],
      };
    default:
      return {
        category: 'lip_color',
        shape: { name: 'original' },
        style: { type: 'full' },
        palettes: [{ color: product.swatch, texture: 'satin', colorIntensity: 50 }],
      };
  }
};

const port = Number(process.env.PORT ?? 8787);
app.listen(port, '0.0.0.0', () => console.log(`Tonalique backend listening on ${port} (${mode})`));
