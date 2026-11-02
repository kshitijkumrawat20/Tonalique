import type {
  AnalysisResult,
  Preferences,
  RecommendationResult,
  TryOnResult,
} from './types';
import { File } from 'expo-file-system';
import { Platform } from 'react-native';

const API_URL = process.env.EXPO_PUBLIC_API_URL?.trim().replace(/\/$/, '');
if (!API_URL) {
  throw new Error('EXPO_PUBLIC_API_URL is required. Configure the live Tonalique backend URL in mobile/.env.');
}

const request = async <T>(path: string, init?: RequestInit): Promise<T> => {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 30000);
  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, {
      ...init,
      signal: controller.signal,
      headers: { 'Content-Type': 'application/json', ...init?.headers },
    });
  } catch (error) {
    if (error instanceof Error && error.name === 'AbortError') {
      throw new Error(`Backend request timed out: ${path}. Check that the current tunnel URL is configured and reachable.`);
    }
    throw new Error('Cannot reach the Tonalique backend. Check the Expo API URL and network connection.');
  } finally {
    clearTimeout(timeout);
  }
  if (!response.ok) {
    const detail = await response.text();
    throw new Error(`Tonalique request failed (${response.status}): ${detail || 'server rejected the request'}`);
  }
  return response.json() as Promise<T>;
};

const wait = (milliseconds: number) => new Promise((resolve) => setTimeout(resolve, milliseconds));

const poll = async <T extends { status: string; error?: string | null }>(
  getTask: () => Promise<T>,
  label: string,
): Promise<T> => {
  for (let attempt = 0; attempt < 36; attempt += 1) {
    const task = await getTask();
    if (task.status === 'success') return task;
    if (task.status === 'error' || task.error) {
      throw new Error(`${label} failed${task.error ? `: ${task.error}` : ''}`);
    }
    await wait(5000);
  }
  throw new Error(`${label} timed out. Please try again.`);
};

export const tonaliqueApi = {
  async createSession(): Promise<{ session_id: string }> {
    return request('/api/v1/sessions', { method: 'POST', body: JSON.stringify({}) });
  },

  async uploadPhoto(sessionId: string, uri: string): Promise<{ media_id: string }> {
    const body = new FormData();
    body.append('session_id', sessionId);
    if (Platform.OS === 'web') {
      const image = await fetch(uri);
      if (!image.ok) throw new Error('The selected photo could not be read by the browser.');
      const blob = await image.blob();
      const browserFile = new globalThis.File([blob], 'selfie.jpg', {
        type: blob.type || 'image/jpeg',
      });
      body.append('file', browserFile);
    } else {
      body.append('file', new File(uri));
    }
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 60000);
    let response: Response;
    try {
      response = await fetch(`${API_URL}/api/v1/media`, { method: 'POST', body, signal: controller.signal });
    } catch (error) {
      if (error instanceof Error && error.name === 'AbortError') {
        throw new Error('Photo upload timed out. The public tunnel may have expired; restart it and update mobile/.env.');
      }
      throw new Error('Photo upload could not reach the backend.');
    } finally {
      clearTimeout(timeout);
    }
    if (!response.ok) {
      const detail = await response.text();
      throw new Error(`Photo upload failed (${response.status}): ${detail || 'server rejected the image'}`);
    }
    return response.json() as Promise<{ media_id: string }>;
  },

  async runAnalysis(sessionId: string, mediaId: string): Promise<AnalysisResult> {
    const task = await request<{ analysis_id: string }>('/api/v1/skin-analyses', {
      method: 'POST',
      body: JSON.stringify({
        session_id: sessionId,
        media_id: mediaId,
        actions: ['skin_type', 'radiance', 'redness', 'oiliness'],
        format: 'json',
      }),
    });
    const result = await poll(
      () => request<{ status: string; result: AnalysisResult; error?: string | null }>(`/api/v1/tasks/${task.analysis_id}`),
      'Skin analysis',
    );
    return result.result;
  },

  async getRecommendations(preferences: Preferences): Promise<RecommendationResult> {
    return request('/api/v1/recommendations', {
      method: 'POST',
      body: JSON.stringify({ ...preferences, currency: 'INR', budget_minor: preferences.budgetMinor }),
    });
  },

  async runTryOn(sessionId: string, mediaId: string, productId: string): Promise<TryOnResult> {
    const task = await request<{ tryon_id: string }>('/api/v1/try-ons', {
      method: 'POST',
      body: JSON.stringify({
        session_id: sessionId,
        media_id: mediaId,
        product_id: productId,
        effect_config_id: 'lip-rosewood-satin-v1',
      }),
    });
    const result = await poll(
      () => request<{ status: string; preview_url: string; disclaimer: string; error?: string | null }>(`/api/v1/try-ons/${task.tryon_id}`),
      'Virtual try-on',
    );
    return { previewUrl: result.preview_url, disclaimer: result.disclaimer };
  },
};
