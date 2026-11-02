# Tonalique — Android Design Specification

**Version:** 0.1  
**Platform:** Android native / Jetpack Compose  
**Design direction:** editorial beauty-tech, warm minimalism, confident and inclusive  
**Tagline:** *Find your tone. Build your look. Try before you buy.*

---

## 1. Design intent

Tonalique should feel like a thoughtful beauty editor, not a clinical diagnostics dashboard and not a generic discount-shopping app.

The interface should communicate:

- **Personal, not prescriptive:** the user chooses their goal and can override every suggestion.
- **Premium, not intimidating:** warm surfaces, generous whitespace and restrained accent colours.
- **Trustworthy:** source and price information is visible; analysis and generated previews are clearly labelled.
- **Visual-first:** the face preview and product swatches are central, with short explanations alongside them.
- **Inclusive:** the UI never treats a particular skin tone, texture, age or facial feature as the default ideal.

## 2. Brand system

### Name

**Tonalique** — a coined combination of “tone” and “boutique”. The name positions the app as a curated beauty studio rather than a cart or generic AI scanner.

### Brand line

**Find your tone. Build your look. Try before you buy.**

### Voice

Warm, clear, informed and non-judgmental.

Prefer:
- “Your preview is ready.”
- “Chosen for your soft-satin preference.”
- “3 items · ₹1,420 total.”
- “This result is a cosmetic reference, not a diagnosis.”

Avoid:
- “Fix your flaws.”
- “Perfect skin.”
- “Your skin is bad.”
- “Guaranteed perfect match.”
- Medical claims or exaggerated certainty.

## 3. Visual identity

### Colour tokens

Use semantic tokens instead of hard-coded values in individual screens.

| Token | Hex | Use |
|---|---|---|
| `canvas` | `#F7F3EF` | Main warm ivory background |
| `surface` | `#FFFCF9` | Cards, sheets and dialogs |
| `surfaceMuted` | `#EEE6E0` | Secondary panels and placeholders |
| `ink` | `#292326` | Primary text |
| `inkMuted` | `#70666A` | Secondary text |
| `plum` | `#704052` | Primary action and selected states |
| `plumPressed` | `#56303F` | Pressed state |
| `rose` | `#D9A9A7` | Soft decorative accent, not body text |
| `rosePale` | `#F1DEDA` | Tags and subtle highlights |
| `olive` | `#596B55` | Success/confirmed states |
| `amber` | `#93651F` | Caution or pending state |
| `error` | `#A33C42` | Error state |
| `border` | `#E4DAD4` | Dividers and outlines |

### Typography

Use local, licensed font assets if adding custom typefaces. Suggested pairing:

- Display/headlines: **DM Serif Display** (or a system serif fallback).
- Body, labels and numbers: **Manrope** (or Roboto/system fallback).

| Style | Size | Weight | Typical use |
|---|---:|---|---|
| Display | 32sp | Regular | Welcome headline, hero statement |
| H1 | 28sp | SemiBold | Main screen title |
| H2 | 22sp | SemiBold | Section title |
| H3 | 18sp | SemiBold | Card title |
| Body | 16sp | Normal | Main body and descriptions |
| Body Small | 14sp | Normal | Helper copy, source details |
| Label | 12sp | Medium | Chips, metadata, category labels |
| Price | 20sp | Bold | Product price and total |

Support Android font scaling. Do not hard-code line heights that clip enlarged text.

### Spacing and shape

- Base spacing scale: 4dp.
- Common page horizontal padding: 20dp.
- Between major sections: 24dp.
- Card inner padding: 16dp.
- Button height: 52dp.
- Minimum interactive target: 48dp.
- Card corner radius: 20dp.
- Input/sheet corner radius: 14–16dp.
- Pill radius: fully rounded.
- Shadows: subtle; prefer border/surface contrast over heavy elevation.

## 4. Navigation structure

Use a three-destination bottom navigation after onboarding:

1. **Discover** — primary flow, scan, results and recommendations.
2. **My Looks** — saved look cards and generated previews.
3. **Profile** — preferences, privacy and app settings.

The MVP may hide `My Looks` if persistence cannot be completed safely. Do not add empty tabs just to make the interface look complete.

### Navigation graph

```text
Welcome
  └── Preferences
       └── PhotoConsent
            └── PhotoPicker / CameraCapture
                 └── PhotoReview
                      └── SkinAnalysisProgress
                           └── SkinInsights
                                └── RecommendationResults
                                     └── ProductDetails
                                          └── TryOnProgress
                                               └── TryOnResult
                                                    └── LookBundle
                                                         └── Save / Share
```

Back navigation should preserve the chosen preferences. Re-running a paid provider task requires a new explicit user action and a clear notice.

## 5. Screen specifications

### 5.1 Welcome

**Purpose:** Explain the app in one glance.

Layout:
- Top: Tonalique wordmark and small descriptor `PERSONAL BEAUTY STUDIO`.
- Hero: editorial image placeholder or gradient card; do not use a stock face as if it were a real result.
- Headline: “A look that feels like yours.”
- Supporting copy: “Explore cosmetic insights, find a look within your budget, and preview it before you decide.”
- Primary CTA: `Create my look`.
- Secondary text: `No account needed for this demo`.

Interaction:
- CTA begins preferences flow.
- Privacy link opens a readable privacy summary.

### 5.2 Preferences

**Purpose:** Capture explicit shopping context before the image is processed.

Fields:
- Occasion: `Everyday`, `Work`, `College`, `Dinner`, `Celebration`, `Custom`.
- Look: `Natural`, `Soft glam`, `Bold`, `Minimal`, `Let me explore`.
- Finish: `Matte`, `Satin`, `Dewy`, `No preference`.
- Optional shade family: `Warm`, `Cool`, `Neutral`, `Not sure`.
- Budget: editable INR amount, quick chips `₹750`, `₹1,500`, `₹2,500`, `Custom`.

Design:
- Use selected cards/chips, not a long form.
- Budget control must show currency and total budget context.
- Add a small line: “You can change these choices anytime.”

Primary CTA: `Continue`.

### 5.3 Photo consent

**Purpose:** Make photo processing transparent before system picker opens.

Content:
- Title: “Your photo, your choice.”
- Explain that the selected photo is sent to Tonalique's backend and then to YouCam to generate the requested analysis/preview.
- State what is retained by Tonalique, for how long, and how to delete the session.
- Link to full privacy notice.
- Unchecked consent checkbox or explicit `I agree and choose a photo` button; never pre-check.

CTA disabled until the user explicitly agrees.

### 5.4 Photo selection and review

**Purpose:** Give the user control over the photo and reduce avoidable provider failures.

Photo guidance:
- Face visible, front-facing, even lighting.
- Avoid strong filters, sunglasses, heavy obstruction and extreme angles.
- Neutral expression, head and chin visible.
- Keep the original image private and never show it in shared UI unless user requests it.

Review actions:
- `Use this photo`
- `Choose another`
- `Cancel`

Show the image crop in a rounded portrait frame. Provide a clear remove/replace action.

### 5.5 Analysis progress

**Purpose:** Explain the asynchronous operation and avoid a blank spinner.

Progress steps:
1. `Preparing photo`
2. `Running cosmetic analysis`
3. `Organising your results`

Show one progress state at a time; don't invent a percentage when the provider does not expose progress. A short note: “This can take a little time. Keep the app open while we finish.”

Failure state:
- Friendly reason where known.
- `Try again` only when safe and credits/duplicate-task status are understood.
- `Choose another photo` option.

### 5.6 Skin insights

**Purpose:** Display actual provider output in approachable language.

Layout:
- Title: “Your skin snapshot”.
- Pill: `Cosmetic reference`.
- 2-column metric cards only for metrics actually returned.
- A visual overlay/result image can be opened full screen.
- `What this means` expands a brief plain-language description based on the provider's own field definition.
- Notice: “This is an AI cosmetic reference, not a medical diagnosis or treatment recommendation.”

Rules:
- Do not turn the metrics into an invented overall attractiveness/beauty score.
- Do not use red/green to judge a user's appearance.
- Missing fields are hidden or shown as `Not available`, never guessed.
- The screen has a clear `Build my look` CTA.

### 5.7 Recommendation results

**Purpose:** Show a short, useful selection that respects budget.

Header example:
- `Your soft-satin edit`
- `3 picks · ₹1,420 of ₹1,500`
- Progress bar or small budget meter (must be accessible without colour alone).

Product card:
- Product image.
- Brand + product name.
- Shade name/family and finish.
- Current verified price and `Price checked` date.
- One grounded reason: “Matches your selected satin finish.”
- `Preview this shade` primary/secondary action.
- `View source` link.

Bundle area:
- Subtotal.
- Remaining budget.
- State explicitly that shipping/tax are excluded unless verified.

Do not show “Best for your skin” unless the match is supported and the logic is explainable. Prefer “Matches your selected preferences”.

### 5.8 Product details

**Purpose:** Explain an individual catalogue match.

Content:
- Product media and shade swatch.
- Brand/product name.
- Verified price, source and checked date.
- Finish, shade and category.
- Match reasons using labelled chips.
- `Preview on my photo` action.
- `Open product page` action.

Avoid invented ingredients, ratings, product benefits or stock availability. Omit data not present in source catalogue.

### 5.9 Virtual try-on progress

Show the selected shade/look and the task stages:
1. `Preparing your preview`
2. `Applying the selected effect`
3. `Finishing your look`

Show a credit/use notice before launching another variant if the provider account charges units per task.

### 5.10 Try-on result

**Purpose:** Make the provider integration obvious and let the user compare before/after.

Layout:
- Main generated image with accessible `Before` / `Preview` switch or a drag comparison slider.
- Badge: `AI preview`.
- Product/shade name that produced this effect.
- Short note: “Rendered colour and finish can vary with lighting, display settings and the real product.”
- CTA: `Add to my look`.
- Secondary: `Try another shade` (show potential credit use), `Save look`.

Do not imply that a rendered preview is a guaranteed physical outcome.

### 5.11 Look bundle

**Purpose:** Close the journey with a clear decision summary.

Show:
- Look name and occasion.
- Selected products and prices.
- Total and remaining budget.
- `Why these picks` expandable explanation.
- `Open product pages` actions.
- Save/share controls.

Do not claim a multi-store cart or checkout exists. This is a shortlist, not an order.

### 5.12 Profile and privacy

Options:
- Edit preferences.
- View privacy summary.
- Delete current session and generated images.
- Clear locally saved look cards.
- App version and open-source notices.

## 6. Reusable components

- `TonaliqueTopBar`
- `BudgetSelector`
- `PreferenceChipGroup`
- `PhotoConsentSheet`
- `SelfiePreviewCard`
- `AnalysisMetricCard`
- `ProductRecommendationCard`
- `PriceAndSourceRow`
- `BudgetSummaryCard`
- `TryOnComparisonView`
- `TaskProgressTimeline`
- `InlineNotice`
- `EmptyState`
- `ErrorState`

## 7. Interaction and motion

- Use short crossfade/slide transitions (150–220ms) between major states.
- Use gentle elevation and scale feedback for selected chips; avoid large bounce animations.
- Keep the task flow calm. No flashing progress or fake real-time scanning effect.
- Pressed/selected states must work with TalkBack and keyboard/switch input.
- Provide haptic feedback only for meaningful completion/selection, and respect device settings.

## 8. Accessibility requirements

- Body text contrast target: WCAG AA where applicable.
- All interactive targets at least 48dp.
- Content descriptions for product image, preview image and all icons with meaning.
- Decorative images have empty descriptions.
- Do not communicate status by colour alone; add text/icon.
- Test Android font scaling and TalkBack.
- Comparison slider must also offer a simple before/after toggle for accessible operation.
- Error messages must identify the issue and next action.

## 9. Compose implementation conventions

- Build semantic theme tokens in `ui/theme/Color.kt`, `Type.kt`, `Shape.kt`.
- Use screen-level state holders/ViewModels and immutable UI state.
- Keep network models separate from UI models.
- Never pass raw provider JSON directly into composables.
- Use `AsyncImage`/Coil with loading/error placeholders.
- Keep image URI, provider file/task IDs and signed URLs out of analytics/logging.
- Prefer a single source of truth for task state in the repository/ViewModel.

## 10. Visual QA checklist

- [ ] Looks intentional on 360dp and 412dp width devices.
- [ ] Keyboard does not cover budget inputs or consent controls.
- [ ] Product names, price and source do not overflow.
- [ ] Face image preserves aspect ratio and is not stretched.
- [ ] Before/after view is usable with TalkBack.
- [ ] Large text mode remains usable.
- [ ] All task states have an understandable visual treatment.
- [ ] Skin metrics do not imply medical diagnosis or beauty ranking.
- [ ] Preview disclaimer appears near generated result, not hidden in settings.
- [ ] Price/source information is readable at a glance.
