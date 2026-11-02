# Tonalique Expo client

This folder is the React Native/Expo replacement for the Kotlin presentation layer. It follows the product flow in [`docs/design.md`](../docs/design.md): preferences, explicit photo consent, photo review, cosmetic insights, catalogue recommendations and virtual try-on.

## Run locally

```powershell
cd mobile
npm install
npm run start
```

The client requires `EXPO_PUBLIC_API_URL` and uses the real backend only. For a local phone run `cd ../backend; npm install; npm run dev`, then set `EXPO_PUBLIC_API_URL` to the backend's LAN address (for example `http://192.168.1.20:8787`). Do not use `localhost` on a physical phone.

The client never contains a YouCam credential. Photo upload and provider calls belong in the backend described in [`docs/ARCHITECTURE.md`](../docs/ARCHITECTURE.md).

## Backend endpoints consumed

- `POST /api/v1/sessions`
- `POST /api/v1/media` (multipart)
- `POST /api/v1/skin-analyses`
- `GET /api/v1/tasks/{task_id}`
- `POST /api/v1/recommendations`
- `POST /api/v1/try-ons`
- `GET /api/v1/try-ons/{tryon_id}`

The API response shapes are the stable contracts in `src/api.ts`; the backend remains responsible for provider validation, task polling, temporary media cleanup and product facts. The repository includes a Node/Express bridge in [`../backend/`](../backend/) so FastAPI is not required. The mobile upload uses Expo FileSystem's `File` object, not a manually cast React Native FormData part.
