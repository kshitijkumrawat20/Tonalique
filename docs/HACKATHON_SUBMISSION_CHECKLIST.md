# Tonalique — Hackathon Submission Checklist

**Event:** YouCam API Skin AI & eCommerce VTO Hackathon  
**Target deadline:** November 2, 2026, 11:45 AM Eastern Time / November 2, 2026, 10:15 PM IST. Verify the official event page before submission.

---

## 1. Required submission assets

- [ ] Project name: **Tonalique**
- [ ] Elevator pitch/tagline: **Find your tone. Build your look. Try before you buy.**
- [ ] Full project description: problem, solution, features, how built, challenges, learnings, next steps.
- [ ] Public GitHub repository.
- [ ] Open-source licence visible at repository root (MIT or Apache-2.0; check dependencies/assets before choosing).
- [ ] Publicly accessible working app / Android install path.
- [ ] Screenshots of primary workflow.
- [ ] Public demo video, 1–3 minutes, preferably public YouTube/Vimeo link.
- [ ] Video visibly demonstrates the intended Android app running.
- [ ] Video explicitly names the YouCam APIs integrated.
- [ ] Public backend endpoint is HTTPS and has no debug/reload mode.
- [ ] No API keys, user selfies or signed URLs in public repo, video or screenshots.

## 2. Recommended 90–150 second demo structure

| Time | Show | Narration |
|---|---|---|
| 0:00–0:12 | App welcome and name | “Tonalique is a budget-first beauty shopping studio.” |
| 0:12–0:28 | Select occasion, finish and budget | “The shopper controls the desired look and maximum spend.” |
| 0:28–0:42 | Consent + photo selection | “The photo is only sent after the user opts in.” |
| 0:42–1:02 | Skin Analysis result | “YouCam Skin Analysis provides cosmetic reference signals.” |
| 1:02–1:22 | Product shortlist and budget total | “Every product is catalogue-grounded and the total stays within the entered budget.” |
| 1:22–1:48 | Makeup VTO before/after | “The selected product shade maps to a YouCam Makeup VTO effect.” |
| 1:48–2:05 | Product source, total and privacy notice | “The user can inspect the product source and decide whether to continue.” |
| 2:05–2:20 | Architecture/repository close | “The native Android client talks to a FastAPI backend; the YouCam key stays server-side.” |

Do not exceed the event's stated 3-minute video cap.

## 3. Screenshots to capture

1. Welcome / Tonalique brand screen.
2. Preferences screen with budget.
3. Photo consent and photo review.
4. Skin insights with cosmetic disclaimer.
5. Product shortlist showing price and bundle total.
6. Before/after Makeup VTO result with preview disclaimer.
7. Optional architecture diagram or event trace.

Use a consented demo photo. Avoid screenshots with real user identity details, API keys, request headers, internal debug logs or temporary signed URLs.

## 4. Repository readiness

- [ ] `README.md` has overview, setup, architecture and demo instructions.
- [ ] `LICENSE` exists at root.
- [ ] `.env.example` documents required secrets but has placeholders only.
- [ ] `.gitignore` excludes `.env`, local image files, build artifacts and keystores.
- [ ] No secrets in git history.
- [ ] Android build instructions work from a clean clone.
- [ ] Backend tests pass.
- [ ] Provider test fixtures are redacted and licensed/allowed for repository use.
- [ ] Catalogue sources are attributed.
- [ ] All screenshots and demo assets are project-owned or permission-cleared.

## 5. Public Android demo path

Preferred hackathon route:

1. Publish a signed APK as a GitHub Release.
2. Include install instructions and an APK checksum in README.
3. Ensure backend URL is already configured or safely changeable without exposing YouCam secret.
4. Test installation on a clean device.
5. If organisers require a URL, make a lightweight public landing page with APK download, video, repository and privacy notice.

Do not assume Google Play production approval is achievable within the hackathon period.

## 6. Final Devpost checks

- [ ] Select the correct hackathon.
- [ ] Confirm project name and tagline.
- [ ] Confirm repository URL is public and accessible.
- [ ] Add public working app/demo or APK URL.
- [ ] Add the 1–3 minute demo video URL.
- [ ] Include screenshots.
- [ ] Clearly identify each YouCam API used.
- [ ] Do not claim features that are only mocked or planned.
- [ ] Reopen the submission after save and verify all links.
- [ ] Click the final **Submit** action and verify Devpost shows submission confirmation/status before deadline.

## 7. Official event reference

- [Hackathon page](https://youcam-api-skin-ai-ecommerce.devpost.com/)
- [Official rules](https://youcam-api-skin-ai-ecommerce.devpost.com/rules)
