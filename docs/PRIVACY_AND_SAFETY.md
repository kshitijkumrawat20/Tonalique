# Tonalique — Privacy, Security & Responsible-Use Requirements

**Status:** MVP requirements  
**Applies to:** Android client, FastAPI backend, temporary storage and YouCam API calls

---

## 1. Privacy principle

A face photo is personal data. Tonalique must process it only after an intentional user action, disclose the external provider, minimise retention and avoid using it for unrelated purposes.

## 2. Consent and transparency

Before each photo upload, show a just-in-time notice explaining:

- The photo is used to run the requested YouCam cosmetic analysis and/or virtual makeup preview.
- The photo is transmitted from the Android app to Tonalique's backend and then to YouCam through the documented API workflow.
- Whether the app stores the original photo temporarily, where and for how long.
- Whether generated results are retained, how the user can delete them, and any provider-side retention controlled by YouCam.
- The user can cancel without uploading.

Consent must be affirmative, unbundled from unrelated terms, and not preselected. Link the full privacy notice from this screen.

## 3. Data minimisation

Collect only what is required:

- Ephemeral session ID.
- Occasion/look/finish/budget preferences.
- User-selected source photo.
- Provider task IDs and normalised result fields required to display the requested result.
- Product shortlist and saved look metadata only if the user chooses to save.

Do not collect or infer race, ethnicity, religion, health diagnosis, identity, emotional state or other sensitive attributes. Do not implement face recognition, identity matching or user identification from facial geometry.

## 4. Image handling requirements

- Prefer Android Photo Picker; do not request broad photo-library access.
- Do not upload until the user presses the upload/continue control after consent.
- Validate file bytes, MIME type and dimensions.
- Strip EXIF metadata before onward transfer where feasible.
- Use HTTPS for every network hop.
- Use a private bucket and short-lived signed read URL if the provider requires a public URL.
- Use a TTL sufficient for provider processing, then delete Tonalique's temporary copy.
- Do not save source selfies to shared external storage by default.
- Do not include photo bytes, local URIs, signed URLs or image names in application logs or analytics.
- Do not attach selfies to crash reports.

## 5. Retention and deletion

Proposed hackathon default:

- Source image: temporary only, deleted by Tonalique after provider processing is complete or the session TTL expires.
- Provider task metadata: retained only long enough to complete the user-visible flow and debug operational failures in redacted form.
- Result preview: temporary provider result URL; keep locally only when user explicitly saves a generated look, and follow provider URL expiry.
- Saved look: product IDs, product facts, user label and result reference only where possible; raw selfie is not included by default.

YouCam's public general API documentation describes processed results as retained for 24 hours after completion. Confirm the current event-account terms and whether source-file retention differs. Do not claim that Tonalique can erase provider-side copies unless the provider offers and the implementation uses a supported deletion method.

Provide `Delete this session` and `Clear saved looks` controls.

## 6. Responsible skin-analysis language

The app is a cosmetic shopping tool, not a health service.

Required language:

> “AI cosmetic reference only. This result is not a medical diagnosis, does not identify a skin disease and does not replace advice from a qualified professional.”

Do not:

- Diagnose acne, rosacea, dermatitis, skin cancer or any other condition.
- Recommend treatment or medication.
- Claim an item will cure, treat or prevent a skin condition.
- Suggest that provider scores measure attractiveness, worth or general health.
- Shame a user over texture, pores, spots, age, redness or other visible features.

Use neutral labels such as “provider-reported oiliness indicator” rather than judgemental labels.

## 7. Product recommendation integrity

- Every product name, price, finish, shade, ingredient and retailer link must come from a source-backed catalogue record.
- Store `source_url` and `source_checked_at` for price and product claims.
- If availability is unknown, show `Availability not verified`.
- If a product is fictional demo data, label it clearly.
- LLM text must not create new product claims, ingredients, ratings or prices.
- Do not imply dermatological suitability from a generic skin score.
- Explain that shipping/tax are excluded unless included in verified totals.

## 8. VTO integrity

- Label all generated images `AI preview` or `Virtual try-on preview`.
- Disclose that lighting, device display, camera exposure and physical product formulation can change the real-world appearance.
- Do not market the preview as an exact shade guarantee.
- Do not use another person's photo without permission.
- Do not make a generated result public by default.

## 9. Security requirements

- Store YouCam API key only in backend secret storage/environment variables.
- Use Bearer authentication only from backend to provider.
- Apply upload size limits, content-type validation, rate limits and per-session request limits.
- Use random, non-guessable session/task IDs.
- Scope temporary media access to the specific session and task.
- Redact credentials, provider URLs, user photos and raw face-related results from logs.
- Use dependency scanning and secret scanning in CI.
- Keep dependencies pinned/locked before final build.

## 10. Abuse and failure handling

- Block unsupported file types and excessive upload sizes.
- Avoid repeated paid provider tasks from rapid button taps.
- Confirm before re-running an analysis or VTO operation that may consume units.
- Show a neutral fallback when analysis is unavailable; do not substitute fabricated skin metrics.
- If a user withdraws consent, cancel future work where possible and delete Tonalique-controlled media.

## 11. Privacy QA checklist

- [ ] Consent is not pre-ticked.
- [ ] No photo request before user action.
- [ ] Photo Picker used instead of broad storage permission when possible.
- [ ] API key absent from APK and git history.
- [ ] Image and signed URL not written to logs.
- [ ] Image files are deleted after configured TTL.
- [ ] Delete-session path is tested.
- [ ] No diagnosis, treatment, or guaranteed product-fit claim appears.
- [ ] Product prices have source and check timestamp.
- [ ] AI preview notice is visible on the result screen.
- [ ] User can use core shopping preference screens without uploading a photo, if they decline consent.

## 12. Legal note

This is an engineering/product checklist, not legal advice. Before a public release, review applicable privacy, consumer-protection, children's privacy, biometric/face-image and advertising rules in the countries where the app will be distributed. Review YouCam's current terms and privacy documentation before processing real users' photos.
