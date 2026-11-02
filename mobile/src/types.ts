export type Preferences = {
  occasion: string;
  look: string;
  finish: string;
  shadePreference: string;
  budgetMinor: number;
};

export type Product = {
  id: string;
  brand: string;
  name: string;
  shade: string;
  finish: string;
  category: string;
  priceMinor: number;
  reason: string;
  sourceUrl: string;
  imageUrl?: string;
  swatch: string;
};

export type Insight = {
  id: string;
  title: string;
  value: string;
  note: string;
};

export type AnalysisResult = {
  insights: Insight[];
  note: string;
};

export type RecommendationResult = {
  currency: string;
  budgetMinor: number;
  totalMinor: number;
  remainingMinor: number;
  items: Product[];
};

export type TryOnResult = {
  previewUrl: string;
  disclaimer: string;
};
