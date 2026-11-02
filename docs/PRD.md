# Product Requirements Document — Tonalique

**Version:** 0.1  
**Status:** Hackathon MVP plan  
**Platform:** Android (native)  
**Hackathon:** YouCam API Skin AI & eCommerce VTO  
**Working tagline:** *Find your tone. Build your look. Try before you buy.*

---

## 1. Executive summary

Tonalique is an Android-first personal beauty shopping studio that helps shoppers make more confident makeup choices. It combines cosmetic skin-analysis signals from YouCam, a curated product catalogue, a budget-aware recommendation engine, and YouCam Makeup Virtual Try-On.

Instead of returning a generic “skin score” or an ungrounded list of products, Tonalique builds a small, transparent look bundle around a shopper's stated occasion, preferences and spending limit. The shopper can preview a mapped makeup effect on their own photo before opening product details or retailer links.

### Product promise

**A useful beauty shortlist you can understand, afford and preview.**

### Product principle

The user is in control. Analysis is optional, the photo is submitted only after a clear action and consent, recommendations expose their reasons, and the user decides whether to save or visit a retailer.

## 2. Problem statement

Online beauty shoppers face three linked uncertainties:

1. **Choice overload:** many products and shades are difficult to compare using product descriptions alone.
2. **Personal fit uncertainty:** shoppers are unsure whether a look, finish or shade direction will suit the appearance they want.
3. **Budget fragmentation:** a suggested routine may look attractive but exceed the shopper's actual budget once multiple products are combined.

Existing skin-analysis and virtual-try-on experiences can solve individual parts of this journey. Tonalique focuses on the decision between them: turn explicit preferences and provider-returned signals into a small, budget-valid catalogue shortlist, then connect one selected item/look to a virtual preview.

## 3. Target users

### Primary persona — The practical beauty shopper

- Buys cosmetics online or researches products before visiting a store.
- Has a clear spending limit.
- Wants a look for a specific context (everyday, college, office, event, evening).
- Prefers a small number of understandable options over a large catalogue.
- Wants to preview a look before deciding.

### Secondary persona — The beauty-curious experimenter

- Enjoys testing colours and finishes.
- Wants to compare a natural and a more expressive look.
- May use the app without purchasing anything.

### Explicit non-target use cases for MVP

- Diagnosis or treatment of dermatological conditions.
- Clinical skin assessment.
- Claims that the app can identify allergies, prescribe skincare, or guarantee a product's compatibility.
- Automated checkout, payment handling or fulfilment.

## 4. Jobs to be done

- “When I need a makeup look for an occasion, help me find a few products that fit my style and budget.”
- “When I'm unsure about a product's appearance, let me preview a representative makeup effect on my own photo.”
- “When I see an analysis result, explain what it means in cosmetic terms without pretending it is a diagnosis.”
- “When I compare a set of products, show me the actual total and why each item is in the set.”

## 5. Product goals and non-goals

### Goals

1. Demonstrate at least one genuine YouCam Skin Analysis integration.
2. Demonstrate a genuine YouCam Makeup VTO integration.
3. Connect a selected catalogue shade/product to a valid, documented VTO effect configuration.
4. Produce a recommendation bundle that is within the user's stated budget.
5. Make the AI/API workflow visible enough for a judge to understand.
6. Provide a usable native Android experience with clear loading, empty, success and failure states.
7. Protect provider credentials and minimise selfie retention.

### Non-goals

- Launch a full beauty marketplace.
- Process payments or create orders.
- Build a custom computer-vision model.
- Claim that a YouCam result is clinically validated.
- Infer protected or sensitive personal attributes.
- Promise a perfect one-to-one physical product colour match from a rendered preview.
- Support every YouCam endpoint in the hackathon window.

## 6. Core experience

### Primary journey

1. **Welcome:** user sees the product promise and starts without creating an account.
2. **Set preferences:** user chooses occasion/look goal, finish, optional shade family, maximum budget and currency.
3. **Photo consent:** user reads a concise notice describing what is sent and why. They choose a new photo or an image from the Android Photo Picker.
4. **Photo quality check:** app checks file type, size and basic framing instructions. Do not silently capture or upload.
5. **Skin analysis:** backend submits the photo to YouCam Skin Analysis. App shows progress and a task status.
6. **Skin insights:** app presents provider-returned cosmetic indicators only, with a non-medical notice. No made-up composite “beauty score”.
7. **Product shortlist:** deterministic matching ranks verified catalogue products using explicit preferences, supported provider fields where relevant, category/finish/shade metadata and budget.
8. **Choose a look:** user selects a lipstick/product/look from the shortlist.
9. **Virtual try-on:** backend builds an allow-listed makeup effect payload and submits the Makeup VTO task to YouCam.
10. **Preview and decide:** app shows original and generated result, the mapped product, its source/price and the total for the selected bundle.
11. **Save/share:** user may save a look card locally or export it. Sharing must not include the original selfie unless user explicitly chooses it.

### Required demo path

A judge should be able to complete the critical journey in under three minutes using a provided demo account-free flow and a known-good sample selfie with explicit permission for demo use:

**Preferences → consent → analysis result → 3 product cards under budget → select shade → VTO result → look bundle.**

Include a fallback demo photo from a person who has provided consent. Never use a random web-found face image.

## 7. Functional requirements

Priority definitions: **P0** = required for hackathon demo; **P1** = useful if time permits; **P2** = post-hackathon.

| ID | Priority | Requirement | Acceptance condition |
|---|---|---|---|
| FR-01 | P0 | Anonymous session | User can start without signup; server issues opaque session ID |
| FR-02 | P0 | Preference capture | User can select occasion, desired finish/look, optional shade preference, budget and currency |
| FR-03 | P0 | Explicit photo consent | User must actively accept before upload; no pre-ticked consent |
| FR-04 | P0 | Photo selection | Android Photo Picker can select a supported image; app validates size/type |
| FR-05 | P0 | Skin Analysis API | Backend successfully submits a supported YouCam Skin Analysis task and retrieves a successful result |
| FR-06 | P0 | Honest result display | UI displays only fields returned by provider and labels the output cosmetic/informational |
| FR-07 | P0 | Product catalogue | Catalogue stores name, category, price, currency, finish, shade metadata, image/source URL and last-checked timestamp |
| FR-08 | P0 | Budget-aware recommendations | Every recommended bundle is within budget or explicitly marked “over budget”; no hidden fees assumed |
| FR-09 | P0 | Match explanation | Every product shows at least one reason tied to user choice or catalogue metadata |
| FR-10 | P0 | Makeup VTO | Backend submits a valid Makeup VTO task and returns a completed result image |
| FR-11 | P0 | Product-to-VTO mapping | Selected catalogue shade maps to a reviewed/allow-listed API effect payload; UI states preview is illustrative |
| FR-12 | P0 | Task progress and errors | queued/processing/success/error states are visible; retry does not silently duplicate paid tasks |
| FR-13 | P0 | Server-side secrets | YouCam API key is stored only in backend environment secrets |
| FR-14 | P0 | Demo path | App can be tested from a clean install with no private developer credentials in the APK |
| FR-15 | P0 | Accessibility basics | Text contrast, content descriptions, scalable text and minimum touch target are checked |
| FR-16 | P1 | Save look locally | User can save a product bundle/result metadata locally without retaining raw selfie by default |
| FR-17 | P1 | Before/after comparison | User can drag a slider or switch tabs between original and generated image |
| FR-18 | P1 | Compare products | User can compare up to three options, including actual price and differences |
| FR-19 | P1 | Share look card | User can share a result card that excludes the source photo unless explicitly selected |
| FR-20 | P2 | Account sync | Optional login and cloud-synced saved looks |
| FR-21 | P2 | Retailer catalogue integrations | Import official product feeds/affiliate catalogues with source attribution |
| FR-22 | P2 | More VTO categories | Foundation, blush, eye, hair or other categories after the base workflow is stable |

## 8. Product matching logic

### Inputs

- User's selected occasion/look goal.
- Maximum total spend and currency.
- Explicitly selected shade family and preferred finish.
- Catalogue fields: category, shade family, colour code when available, finish, price, availability/source status and product URL.
- A small, explicitly allow-listed set of cosmetic signals from YouCam analysis, if the team verifies a defensible mapping.

### Matching rules

1. Hard-filter products with missing or stale price/source data from purchase-oriented recommendations.
2. Filter to the desired product category and finish where the user supplied a preference.
3. Rank using transparent metadata match features.
4. Assemble a bundle whose sum does not exceed the user's budget.
5. Show trade-offs if the desired bundle cannot fit (e.g. one premium item plus one lower-priced alternative).
6. Never map an acne/age/wrinkle score to a claim that a cosmetic product treats that issue.
7. Do not infer undertone from skin-analysis scores. Use a supported YouCam skin-tone endpoint only after separate validation, or let users select a shade family themselves.
8. Do not treat a makeup VTO hex colour as an exact physical SKU match unless the brand provides a documented shade mapping.

### Simple scoring baseline

A deterministic score is sufficient for MVP:

`score = 0.35 * stated_goal_match + 0.25 * finish_match + 0.20 * shade_preference_match + 0.10 * category_coverage + 0.10 * price_fit`

All components are normalised to 0–1 and stored with a short explanation. If a field is unavailable, omit/renormalise that component rather than fabricate a value. The bundle optimiser prioritises highest-ranked items while keeping total cost at or below the budget.

**Important:** This is a product-ranking heuristic, not a scientifically validated skin-fit score.

## 9. Product catalogue requirements

Each catalogue item must contain:

```json
{
  "product_id": "sku-demo-001",
  "name": "Example Lip Colour",
  "brand": "Example brand",
  "category": "lipstick",
  "shade_name": "Rosewood",
  "shade_family": "warm_rose",
  "hex_preview": "#A95B68",
  "finish": "satin",
  "price_minor": 79900,
  "currency": "INR",
  "image_url": "https://example.com/product.jpg",
  "product_url": "https://example.com/product-page",
  "source_name": "Official product page",
  "source_checked_at": "2026-10-31T00:00:00Z",
  "availability_status": "unknown",
  "vto_effect_config_id": "lip-rosewood-satin-v1"
}
```

`price_minor` is integer minor currency units (₹799.00 = 79,900 paise) to avoid floating-point arithmetic. If the catalogue uses whole rupees, state that convention consistently. This example is schema-only, not a real product listing.

For the demo, use 12–20 products with source-backed metadata. If you use fictional sample products, mark the catalogue clearly as demo data and don't link to fictional purchase pages.

## 10. Non-functional requirements

| Area | Requirement |
|---|---|
| Performance | Preferences/catalogue screens render locally; show task progress during provider processing |
| Reliability | HTTP timeouts, bounded polling, retries only for safe/idempotent operations, clear provider error mapping |
| Security | TLS, server-side API key, MIME/size validation, rate limiting, no image bytes in logs |
| Privacy | User-initiated upload, explicit consent, short retention, delete operation, privacy notice |
| Accessibility | TalkBack labels, 48dp touch targets, contrast, text scaling, non-colour-only status cues |
| Maintainability | API client isolated from route handlers; provider response mapped to stable internal schemas |
| Testability | Unit tests for ranking, budget constraints, API payload builders, task-state mapping and error handling |
| Transparency | Explain why each product is shown; disclose catalogue timestamp and preview limitations |

## 11. Analytics and success metrics

For hackathon measurement, keep analytics minimal and non-sensitive. Do not log selfie images, face descriptors or provider raw payloads.

Suggested MVP success metrics:

- **Task completion:** percentage of demo sessions reaching the VTO result.
- **Recommendation validity:** 100% of bundles marked “within budget” sum to no more than the entered budget.
- **Source completeness:** 100% of product cards have a source URL and price timestamp or are visibly marked demo-only.
- **Provider success:** record task success/error and latency without storing image data.
- **User comprehension:** a tester can explain why at least one product was recommended.
- **Recovery:** provider error produces a retry or clear restart path, not a blank screen.

## 12. Risks and mitigations

| Risk | Mitigation |
|---|---|
| YouCam upload/payload schema differs by API version | Build a small integration spike first; save known-good request/response fixtures |
| Provider credits are consumed during demos | Show pre-run notice; cache completed results per ephemeral session; limit VTO variants |
| Product shade is not identical to the VTO palette | Label preview illustrative; make no exact shade guarantee |
| Catalogue metadata becomes stale | Store source and checked timestamp; hide stale/unverified prices |
| Image quality causes poor results | Provide capture guide and retry before calling provider |
| Slow asynchronous task | Show progress, bounded polling, cancel UI and task recovery |
| Face-photo privacy concern | Just-in-time consent, minimum retention, delete controls and no image logging |
| Over-engineered agents delay delivery | Use a bounded orchestrator and deterministic matcher; postpone LLM/agent framework |

## 13. Hackathon acceptance criteria

The MVP is ready to submit when:

- [ ] A fresh Android install can complete the primary journey.
- [ ] A real YouCam Skin Analysis request returns a successful task result.
- [ ] A real YouCam Makeup VTO request returns a generated preview.
- [ ] API key is not present in APK, git history, screenshots or logs.
- [ ] Budget totals pass automated tests.
- [ ] Product facts are source-backed or clearly marked demo data.
- [ ] Skin-analysis UI contains the non-medical notice.
- [ ] VTO UI contains the illustrative-preview notice.
- [ ] The app has loading, no-network, quota/credit, invalid-image, provider-error and success states.
- [ ] A public repository, licence, screenshots and a 1–3 minute demo video are ready.
- [ ] The installation path is public and understandable to judges.

## 14. Competitive positioning

A public hackathon-related project already describes a flow combining skin analysis, shade recommendations and makeup virtual try-on. Tonalique should therefore avoid presenting “analysis + try-on” as its only novelty. Its sharper position is:

> **The budget-first beauty decision companion:** every recommended item has a source, a reason, a current price record, and a place in a total-cost look bundle; the selected shade can then be previewed.

The difference should be visible in the demo, not just in the pitch.

## 15. Open questions to resolve in the first implementation spike

1. Which YouCam file-input mode is accepted by Skin Analysis v2.1 in the hackathon account: `src_file_url`, `src_file_id`, or both? Current reference documents `src_file_url` as required and publicly accessible.
2. Does the hackathon account support `format: "json"` for Skin Analysis v2.1, and which exact SD action set gives a useful result within unit limits?
3. Which exact Makeup VTO `effects` payloads are enabled for the account and what is the unit cost per task?
4. Can the same uploaded image/file ID be reused across the two APIs, or does each feature require a feature-specific file upload?
5. Which publicly accessible APK hosting path will be used for judges?

These are engineering spikes, not reasons to delay the Android shell or product matcher.

## 16. References

- [Hackathon rules](https://youcam-api-skin-ai-ecommerce.devpost.com/rules)
- [YouCam Quick Start](https://docs.perfectcorp.com/develop/quick_start_guide)
- [YouCam Skin Analysis v2.1](https://docs.perfectcorp.com/reference/ai_skin_analysis/v2.1)
- [YouCam Makeup VTO v1.0](https://docs.perfectcorp.com/reference/makeup_vto/v1.0)
- [YouCam agent skills](https://docs.perfectcorp.com/develop/agent_skills)
- [Public competing concept: Undertone](https://github.com/Chinmay-92/undertone)
