# Backend implementation target

The repository now includes a Node/TypeScript implementation in [`../../backend/`](../../backend/). FastAPI is not required for the Expo client.

The Node backend responsibilities:

- Keep `YOUCAM_API_KEY` server-side.
- Validate and temporarily process selfie uploads.
- Implement YouCam Skin Analysis v2.1 and Makeup VTO v1.0 clients.
- Use a bounded task state machine and safe task polling.
- Serve the verified product catalogue and deterministic budget matcher.
- Map product IDs to reviewed VTO effect payloads.
- Clean up media and keep logs free of image bytes, keys and signed URLs.

Run the local end-to-end demo:

```powershell
cd backend
npm install
npm run dev
```

The backend runs in live provider mode and returns explicit provider/configuration errors rather than fabricating results. Validate the account-specific YouCam upload workflow and effect schema described in [`../YOUCAM_API_INTEGRATION.md`](../YOUCAM_API_INTEGRATION.md) before spending credits.

The public YouCam REST documentation does not provide an official Expo/React Native SDK for these workflows. The API key and any private credential must remain in this backend; never put them in `EXPO_PUBLIC_*` variables or the mobile bundle.

Read `../ARCHITECTURE.md`, `../YOUCAM_API_INTEGRATION.md` and `../PRIVACY_AND_SAFETY.md` before enabling live provider calls.
