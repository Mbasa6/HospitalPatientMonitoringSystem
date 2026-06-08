# Merged Pull Requests — Assignment 15

All four pull requests were reviewed, CI-verified, and merged by the repository owners.

---

## PR 1 — ElectoView

**Repository:** https://github.com/MaleselaModiba123/ElectoView
**Pull Request:** https://github.com/MaleselaModiba123/ElectoView/pull/34
**Issue:** #17 — Add structured validation error responses
**Branch:** `fix/issue-17-clean`
**Status:** Merged

### Summary of changes
- Added `GlobalExceptionHandler.java` in `za.ac.cput.controller` annotated with `@RestControllerAdvice`.
- Handles `IllegalArgumentException` (400), `IllegalStateException` (409), and `RuntimeException` (404 or 500) with a structured JSON response containing `timestamp`, `status`, `error`, and `message` fields.
- Removed a duplicate `GlobalExceptionHandler.java` from `za.ac.cput.exception` (test package) that was causing a Spring bean conflict and CI failure.

---

## PR 2 — campus-lost-and-found

**Repository:** https://github.com/Skiet88/campus-lost-and-found
**Pull Request:** https://github.com/Skiet88/campus-lost-and-found/pull/50
**Issue:** #26 — Add edge-case tests for ItemReportBuilder
**Branch:** `fix/issue-26-itemreportbuilder-edge-tests`
**Status:** Merged

### Summary of changes
- Created `tests/creational_patterns_tests/Itemreportbuilder.edge.test.js` with 14 new Jest tests.
- Tests cover: per-field required validation (userId, type, title, description, location, dateLostFound individually), empty string / falsy value handling, Director reset behaviour across consecutive builds, and `buildFullFoundReport` after `buildMinimalLostReport`.

---

## PR 3 — university-research-collaboration-platform (pagination)

**Repository:** https://github.com/Ngandana/university-research-collaboration-platform
**Pull Request:** https://github.com/Ngandana/university-research-collaboration-platform/pull/68
**Feature:** Add skip/limit pagination to list endpoints (README feature-request)
**Branch:** `docs/issue-63-reflection`
**Status:** Merged

### Summary of changes
- Added `skip` (default: 0) and `limit` (default: 100, max: 1000) query parameters to `GET /api/users`, `GET /api/projects`, and `GET /api/tasks`.
- All 249 existing tests pass unchanged — defaults preserve previous behaviour exactly.

---

## PR 4 — university-research-collaboration-platform (document upload)

**Repository:** https://github.com/Ngandana/university-research-collaboration-platform
**Pull Request:** https://github.com/Ngandana/university-research-collaboration-platform/pull/69
**Issue:** #4 — Upload Research Document (US-004)
**Branch:** `feat/issue-4-document-upload`
**Status:** Merged

### Summary of changes
- Added `POST /api/documents/upload` endpoint accepting multipart file upload with `title` and `uploader_id` form fields.
- Validates file extension (PDF or DOCX only) and file size (≤50 MB) using constants from the existing `Document` domain model.
- Wired `InMemoryDocumentRepository` via `RepositoryFactory`.
- Added `python-multipart` to `requirements.txt` and the CI install step to support FastAPI form/file handling.
- Returns 201 with document metadata on success; 400 for invalid type or size; 404 for unknown uploader.
