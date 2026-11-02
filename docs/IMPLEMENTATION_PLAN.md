# Tonalique — Hackathon Implementation Plan

**Planning date:** October 31, 2026  
**Target submission deadline:** November 2, 2026, 10:15 PM IST (verify on event page)  
**Strategy:** prove the two provider calls first, build one polished end-to-end Android journey, and avoid optional infrastructure.

---

## 1. Delivery strategy

Do not build a broad marketplace. Build one complete, convincing vertical slice:

**Preferences → consented selfie → real Skin Analysis → budget-valid product shortlist → real Makeup VTO → before/after preview → transparent look summary.**

A thin Android shell and FastAPI backend are more important than extra agents or a large catalogue.

## 2. Workstreams and dependency order

### P0 — Provider spike (first)

- [ ] Confirm hackathon YouCam API key works.
- [ ] Verify exact source-image upload flow for Skin Analysis v2.1.
- [ ] Run one Skin Analysis task with a supported low-cost action set.
- [ ] Verify JSON result structure and result URL lifecycle.
- [ ] Verify one Makeup VTO lip-colour payload and result retrieval.
- [ ] Record current unit usage for both APIs.
- [ ] Save redacted fixtures for success/error responses.

**Exit condition:** both APIs complete a real task against one consented test image.

### P0 — Backend vertical slice

- [ ] FastAPI app health endpoint and environment config.
- [ ] `YouCamClient` with auth, timeout and error mapping.
- [ ] Media upload preparation and cleanup.
- [ ] Skin Analysis task create/status endpoints.
- [ ] Makeup VTO task create/status endpoints.
- [ ] Product catalogue JSON with 12–20 verified demo products.
- [ ] Deterministic product ranking and budget calculation.
- [ ] Allow-listed VTO effect registry.
- [ ] Unit tests for payloads, task statuses and budget rules.

**Exit condition:** API can run the complete workflow without manual edits to provider requests.

### P0 — Android flow

- [ ] Compose project scaffold and theme.
- [ ] Welcome and preference screens.
- [ ] Consent + Photo Picker + photo review.
- [ ] Skin analysis progress and result screen.
- [ ] Product shortlist + product details.
- [ ] Makeup VTO progress and result screen.
- [ ] Look bundle summary.
- [ ] Network error and retry states.
- [ ] TalkBack labels, font scaling and touch target check.

**Exit condition:** a fresh install can complete the demo journey.

### P0 — Deploy and prove

- [ ] Deploy backend over HTTPS with API key stored as secret.
- [ ] Build signed release APK.
- [ ] Publish APK via public GitHub Release or approved public download.
- [ ] Install APK on a second Android device/emulator.
- [ ] Test with a consented sample photo on mobile data and Wi-Fi.
- [ ] Capture screenshots and record the demo.

**Exit condition:** another person can install and demonstrate without developer credentials.

## 3. Suggested two-day sequence

| Window (IST) | Focus | Deliverable |
|---|---|---|
| Oct 31 evening | YouCam API spike | Real Skin Analysis + Makeup VTO results; source-field decision |
| Nov 1 morning | Backend | Media handling, provider client, tasks and error states |
| Nov 1 afternoon | Android UI | Onboarding/preferences/photo/analysis/result screens |
| Nov 1 evening | Recommendations + VTO | Curated catalog, budget matcher, preview flow |
| Nov 2 morning | Integration QA | APK, backend deployment, end-to-end test |
| Nov 2 afternoon | Submission assets | README, licence, screenshots, 1–3 min video, Devpost text |
| Nov 2 before 10:15 PM IST | Final submission | Verify link, upload and submit before deadline |

This schedule is a prioritisation guide, not a promise that every optional feature will fit. If time compresses, drop saved looks, compare UI and LLM explanations before dropping either real YouCam integration.

## 4. Build order inside the repo

```text
Tonalique/
  android/
  backend/
  catalog/
    demo_products.json
  docs/
  .env.example
  .gitignore
  README.md
  LICENSE
```

Recommended implementation order:

1. Create API contract tests and provider smoke scripts.
2. Complete real YouCam calls and fixtures.
3. Implement backend endpoints and local catalogue ranker.
4. Build Android app using mocked backend fixtures while server work continues.
5. Connect Android to deployed backend.
6. Add only polish that improves primary demo clarity.

## 5. Definition of done per feature

### Skin Analysis

- Real call succeeds.
- Result parser tested against real fixture.
- UI does not invent unavailable fields.
- Failure/credit-exhaustion case is handled.

### Product matching

- Catalogue source fields populated.
- Budget calculation uses integer minor units.
- All products have a grounded reason.
- The bundle total passes automated checks.

### Makeup VTO

- Validated provider payload stored in registry.
- Task result retrieved.
- Product/effect mapping shown in UI.
- Result disclaimer visible.

### Android

- No hard crash on denied permissions, back navigation or network loss.
- User can cancel before upload.
- User sees meaningful progress and recoverable errors.

## 6. Optional items to cut first

1. Multi-agent/LangGraph orchestration.
2. User accounts and cloud sync.
3. Payment/cart/checkout.
4. Complex motion design.
5. Multiple VTO effect categories.
6. Social sharing beyond an exported static look card.

## 7. Final rehearsal

Run this checklist once on a device that has never run the debug build:

- [ ] Install APK from the public link.
- [ ] Start without login.
- [ ] Set a ₹1,500 budget and soft-satin look.
- [ ] Accept photo-processing consent.
- [ ] Select the permitted demo selfie.
- [ ] Wait for real Skin Analysis result.
- [ ] Confirm at least three catalogue options and total.
- [ ] Select a lipstick shade and launch VTO.
- [ ] Inspect before/after result and disclaimer.
- [ ] Open a product source link.
- [ ] Delete the session.
- [ ] Confirm logs and screenshots contain no secrets or private photo.

## 8. Critical decision gate

If the Skin Analysis input workflow cannot be validated quickly, do not fake a result. Keep the screen and response fixture for UI development, but clearly mark it as mock during local work. For the submitted demo, either get the real provider request working or simplify to a real VTO-only demo while being transparent about the missing integration. The PRD's target remains both integrations.
