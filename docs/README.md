# Tonalique — Android Product & Engineering Pack

> **Find your tone. Build your look. Try before you buy.**

Tonalique is an Android-first, AI-assisted beauty shopping studio for people who want to explore makeup looks, understand cosmetic skin-analysis signals, compare products against a budget, and preview a selected look on their own photo before deciding what to buy.

This repository contains the product-planning pack and the first React Native/Expo client for the **YouCam API Skin AI & eCommerce VTO Hackathon**. The client lives in [`../mobile/`](../mobile/) and currently supports a local mock mode while the FastAPI backend is implemented.

## Product direction

Tonalique is deliberately more than a skin scanner and more than a virtual makeup mirror. Its core loop is:

1. The shopper states the occasion, preferred finish/look, and maximum spend.
2. The shopper explicitly chooses a selfie and consents to processing.
3. YouCam Skin Analysis returns cosmetic analysis results for display as informational signals.
4. A deterministic product matcher ranks a curated, source-backed catalogue against the shopper's selected goal, preferences and budget.
5. The shopper picks a product/look and the app calls YouCam Makeup Virtual Try-On to preview the mapped makeup effect.
6. The app assembles a transparent look card with products, prices, match reasons, estimated total and source links.

**Differentiator:** budget-first, explainable product selection. The app must not invent products, prices, ingredients, performance claims, or exact product-match certainty. The virtual try-on is a visual preview and not a guarantee of the exact physical shade or finish.

## Read these docs in order

| File | Purpose |
|---|---|
| [`PRD.md`](PRD.md) | Product requirements, users, use cases, scope and success criteria |
| [`design.md`](design.md) | Android visual system, navigation, screen specifications and interaction details |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | Android/backend architecture, data flow and API contracts |
| [`YOUCAM_API_INTEGRATION.md`](YOUCAM_API_INTEGRATION.md) | Provider API workflow, endpoint references and integration spikes |
| [`PRIVACY_AND_SAFETY.md`](PRIVACY_AND_SAFETY.md) | Photo consent, retention, security and responsible UX |
| [`IMPLEMENTATION_PLAN.md`](IMPLEMENTATION_PLAN.md) | Hackathon delivery plan and priority order |
| [`HACKATHON_SUBMISSION_CHECKLIST.md`](HACKATHON_SUBMISSION_CHECKLIST.md) | Demo, repository, video and Devpost checklist |

## Recommended implementation stack

### Mobile client

The current client target is Expo/React Native:

- Expo SDK 54
- React Native 0.81
- Expo Image Picker for consented Photo Picker access
- Typed backend adapter in `mobile/src/api.ts`

The original Kotlin/Compose prototype remains in `app/` as a design reference. The mobile client does not contain a YouCam key and switches to the documented backend when `EXPO_PUBLIC_API_URL` is set.

### Original Android client

- Kotlin
- Jetpack Compose + Material 3
- Navigation Compose
- CameraX only if the Android Photo Picker is insufficient for the first demo
- Coil for image loading
- Retrofit + OkHttp
- Kotlin Coroutines + Flow
- DataStore for non-sensitive preferences
- WorkManager only if background task recovery is required; keep the initial task flow in the foreground

### Backend

- Python 3.12
- FastAPI + Pydantic
- HTTPX for YouCam REST calls
- PostgreSQL optional; a small local catalogue JSON file is sufficient for the hackathon MVP
- Private object storage with short-lived signed URLs for temporary source images
- Pytest

The Expo migration also includes an equivalent Node/TypeScript bridge in [`../backend/`](../backend/) for teams that do not want to run FastAPI. It keeps provider credentials server-side and defaults to a local mock mode until the account-specific YouCam upload workflow is verified.

### AI boundaries

- YouCam is the image-analysis and virtual-try-on provider.
- A deterministic ranking service selects products from verified catalogue fields.
- An LLM is optional and should only produce short explanations from supplied facts. It must not create product facts or medical advice.
- Do not add a multi-agent framework unless it is needed for a testable workflow. A visible, bounded orchestration state machine is enough for the MVP.

## MVP cutline

**Must ship:** Android onboarding/preferences, consented selfie upload, one working YouCam Skin Analysis flow, product ranking from a small verified catalogue, one working Makeup VTO flow, before/after result, and a public downloadable APK/demo path.

**Should ship:** saved look cards, comparison, progress states, accessibility checks, event trace and robust retry handling.

**Not in the hackathon MVP:** payments, user accounts, social feed, dermatologist/medical recommendations, unrestricted LLM product search, push notifications, subscriptions, or production-scale retailer integrations.

## Current planning assumptions

- Primary market/demo currency: INR (₹), but all prices are stored as integer minor units plus an ISO currency code.
- Default test budget: ₹1,500; user can change it.
- App is Android-native. FastAPI is required to protect the YouCam key and coordinate tasks.
- No real customer purchase is processed. Product cards link to a retailer/brand source when one is available.
- Demo catalogue entries must be real and source-backed, or clearly labelled as fictional demo products.
- Provider request fields, allowed actions, file upload method and unit consumption must be checked against the current account docs and a live smoke test before implementation is locked.

## Hackathon timing

The official rules page currently lists the submission deadline as **November 2, 2026 at 11:45 AM Eastern Time**, which is **November 2, 2026 at 10:15 PM India Standard Time** after the US daylight-saving transition. Recheck the event page before final upload in case organisers update it.

## Official references

- [Hackathon page](https://youcam-api-skin-ai-ecommerce.devpost.com/)
- [Hackathon rules](https://youcam-api-skin-ai-ecommerce.devpost.com/rules)
- [YouCam developer introduction](https://docs.perfectcorp.com/develop/introduction)
- [YouCam Quick Start Guide](https://docs.perfectcorp.com/develop/quick_start_guide)
- [YouCam Skin Analysis API v2.1](https://docs.perfectcorp.com/reference/ai_skin_analysis/v2.1)
- [YouCam Makeup VTO API v1.0](https://docs.perfectcorp.com/reference/makeup_vto/v1.0)
- [YouCam API agent skills and workflows](https://docs.perfectcorp.com/develop/agent_skills)

## Naming note

**Tonalique** is a working product name chosen for this hackathon. A web search is not legal clearance. Before a public launch, check Indian and relevant international trademark registries, app-store names, domains and social handles with appropriate professional advice.
