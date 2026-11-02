# YouCam API Integration Plan

**Purpose:** implement and validate the two provider integrations required by the Tonalique MVP.  
**Provider:** Perfect Corp / YouCam API  
**Base URL shown in current references:** `https://yce-api-01.makeupar.com`

---

## 1. Authentication

Current YouCam REST documentation uses Bearer authentication:

```http
Authorization: Bearer YOUR_API_KEY
Content-Type: application/json
```

Keep the key in the FastAPI backend environment (`YOUCAM_API_KEY`). Never package it in Android resources, BuildConfig, remote config, source code, crash reports or screenshots.

## 2. General asynchronous workflow

The documented pattern is:

1. Prepare an image by using the relevant File API and completing the upload to the URL returned by that API, **or** provide a provider-supported publicly accessible source URL.
2. Submit a feature task with `POST` and receive a `task_id`.
3. Poll the matching task endpoint until `task_status` is `success` or `error` (or use a verified webhook if supported for that feature/account).
4. On success, parse the returned results and result URL/JSON.
5. Keep result links temporary and never log signed URLs.

A File API response does not mean the image has uploaded. The returned upload destination must receive the actual file bytes before the AI task is started.

## 3. Skin Analysis v2.1

### Endpoint

`POST /s2s/v2.1/task/skin-analysis`

### Source field caveat

The current public v2.1 reference documents `src_file_url` as required and states that the URL must be publicly accessible. Do not assume `src_file_id` is accepted by this specific v2.1 endpoint just because other YouCam endpoints accept file IDs.

### Proposed MVP request shape

```json
{
  "src_file_url": "https://temporary-public-read-url.example/selfie.jpg",
  "dst_actions": [
    "skin_type",
    "radiance",
    "redness",
    "oiliness"
  ],
  "format": "json"
}
```

The actions above are proposed SD actions from the v2.1 reference. Confirm they are enabled for the event account and do not mix `hd_*` actions with SD actions. The API reference describes both HD and SD sets and says they cannot be mixed. `format: "json"` returns a JSON response; ZIP mode returns a downloadable package with `score_info.json` and detection images. For an Android MVP, JSON is simpler if it exposes all required values.

### Polling

Use the task ID returned from the POST and the documented Skin Analysis task GET endpoint in the same API reference. Poll at a moderate interval (the reference suggests around 10 seconds when polling is used) and stop on terminal state. Use backoff and a maximum task deadline in the backend.

### UI mapping

- Render only supported fields actually present in the response.
- Store raw provider JSON only temporarily for debugging; normalise and redact it.
- Map result fields to neutral descriptions from provider documentation.
- No medical interpretation and no invented aggregate score.

## 4. Makeup Virtual Try-On v1.0

### Endpoints

- `POST /s2s/v2.0/task/makeup-vto`
- `GET /s2s/v2.0/task/makeup-vto/{task_id}`

### Supported request fields

The current v1.0 reference documents either a source URL or a source file ID, plus an `effects` array. The effect object must have a supported category and match its exact schema. Use `version: "1.0"` for the published effect schema.

### Illustrative lip-colour effect shape

The exact palette schema must be checked against the selected effect and account. The current API guide includes a lip-colour structure based on shape and palette entries. An illustrative payload fragment:

```json
{
  "src_file_url": "https://temporary-public-read-url.example/selfie.jpg",
  "version": "1.0",
  "effects": [
    {
      "category": "lip_color",
      "shape": { "name": "original" },
      "palettes": [
        {
          "color": "#A95B68",
          "texture": "matte",
          "colorIntensity": 50
        }
      ]
    }
  ]
}
```

This is a template for a controlled MVP effect, not a guarantee that every palette/shape/texture combination is enabled. Validate a known-good sample payload through the account playground/API before mapping product shades.

### Mapping catalogue products to VTO

Each catalogue product may contain an `effect_config_id`. Backend `effect_registry.py` maps that ID to a reviewed JSON payload. The Android app submits only the ID, not arbitrary raw provider effects.

Start with one or two effects (e.g. lip colour and a basic skin-smoothing effect) rather than trying every supported category. A generated effect is an illustrative simulation, not proof that a physical SKU will look identical in every lighting condition or on every display.

## 5. File upload strategy — integration spike required

The YouCam Quick Start explains that File API returns a `requests.url` upload destination and a `file_id`, and that the client must perform the actual `PUT` upload to that destination.

Before building the full media service, validate:

- Exact file endpoint path for Skin Analysis and Makeup VTO in the current OpenAPI schema.
- Required metadata passed to the File API (file name, size, content type, etc.).
- Exact method/headers for the signed `PUT` URL.
- Whether the same provider file ID can be reused by both APIs.
- Whether Skin Analysis v2.1 can accept file ID or must receive a publicly readable URL.
- How long provider upload URLs and source files remain valid.

**Fallback:** if Skin Analysis v2.1 requires a public `src_file_url`, store the user image in a private object store and generate a temporary signed HTTPS GET URL with a short TTL long enough for the provider task to fetch it. Delete the object after task completion or expiry. Confirm provider reachability before relying on this route.

## 6. Credit and request discipline

- Check the current event-account unit cost for each selected feature before coding the final flow.
- Use SD skin-analysis actions for the MVP unless HD output is necessary and budgeted.
- Do not run analysis on every image change or every slider interaction.
- Reuse a completed analysis result in the same ephemeral session when the user changes only product filters.
- Before each additional VTO task, tell the user that another provider request may consume units.
- Avoid automatic retry of paid POST tasks after ambiguous network timeouts. First query task/session state.
- Keep a local development fixture of successful and failed provider responses so UI work does not consume credits.

## 7. Smoke-test plan

### Skin Analysis smoke test

1. Confirm API key works with a harmless test endpoint/playground if one is provided.
2. Upload one consented test image using the currently documented source flow.
3. Submit one low-cost supported SD configuration.
4. Capture the task ID.
5. Poll until `success` or `error`.
6. Save a redacted JSON fixture and note unit use.

### Makeup VTO smoke test

1. Reuse the source image only if YouCam allows the same file/input mode.
2. Submit one known-good lip-colour effect copied/validated against the official schema.
3. Poll the returned task ID.
4. Download and inspect the result image.
5. Verify the chosen catalogue product and effect configuration remain linked in Tonalique's result record.

## 8. Expected provider errors to handle

- Invalid/missing key.
- Unsupported action/effect or schema validation error.
- Unsupported file URL or expired upload URL.
- File upload incomplete.
- Insufficient units.
- Rate limiting.
- Invalid/no face detected or unsupported image.
- Provider processing error or task timeout.

Show concise user-facing messages; log only redacted status/code/task metadata. Raw face images, API keys, signed URLs and unnecessary raw response payloads must not enter application logs.

## 9. Official references

- [YouCam Quick Start Guide](https://docs.perfectcorp.com/develop/quick_start_guide)
- [YouCam API server](https://docs.perfectcorp.com/develop/api_server)
- [Skin Analysis API v2.1](https://docs.perfectcorp.com/reference/ai_skin_analysis/v2.1)
- [Makeup VTO API v1.0](https://docs.perfectcorp.com/reference/makeup_vto/v1.0)
- [Makeup VTO overview](https://docs.perfectcorp.com/reference/makeup_vto/section/overview)
- [YouCam agent skills](https://docs.perfectcorp.com/develop/agent_skills)
