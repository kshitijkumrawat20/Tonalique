# Architecture Decision Records

## ADR-001 — Native Android app

**Decision:** Kotlin + Jetpack Compose + Material 3.  
**Reason:** The user explicitly wants an Android app; native photo picker, lifecycle and accessibility behaviours are important to the primary flow.  
**Trade-off:** More UI implementation than a responsive web wrapper. Backend remains independently testable.

## ADR-002 — FastAPI protects YouCam credentials

**Decision:** Android calls Tonalique FastAPI; FastAPI calls YouCam.  
**Reason:** Never embed provider credentials in an APK. Backend also normalises provider responses, enforces quotas, and owns temporary file cleanup.

## ADR-003 — Deterministic product matching for MVP

**Decision:** Use a transparent, testable rules/ranking engine over a verified catalogue.  
**Reason:** Budget totals and product facts must be reproducible. LLM-generated price/ingredient claims are unacceptable. A text-generation model is optional for explanation only.

## ADR-004 — One complete workflow over broad feature count

**Decision:** Implement one skin-analysis flow, a small catalog and one Makeup VTO look.  
**Reason:** A real, polished primary journey is stronger than numerous mocked endpoints.

## ADR-005 — Temporary selfies; no default cloud profile

**Decision:** Do not retain raw source images after processing by default; saved looks store metadata unless the user explicitly exports an image.  
**Reason:** Minimises sensitive image retention and reduces backend complexity for the hackathon.
