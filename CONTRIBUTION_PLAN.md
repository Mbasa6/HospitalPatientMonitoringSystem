# Contribution Plan — Assignment 15

## Selected Projects

| Project | Owner | Language | CONTRIBUTING.md | Selected Issue |
|---|---|---|---|---|
| ElectoView | MaleselaModiba123 | Java / Spring Boot | Yes | Issue #17 — structured API error responses |
| campus-lost-and-found | Skiet88 | JavaScript / Node.js | Yes | Issue #26 — edge-case tests for ItemReportBuilder |
| university-research-collaboration-platform | Ngandana | Python / FastAPI | Yes | README feature — pagination; Issue #4 — document upload |

## Why These Projects

All three repositories have a clear `CONTRIBUTING.md`, active issue trackers with `good-first-issue` labels, and passing CI pipelines — making them suitable targets for high-quality contributions.

## Contribution Strategy

1. **Read CONTRIBUTING.md first** in each repo to understand coding conventions, branch naming, and PR requirements.
2. **Start with tests and focused fixes** (lower risk, faster CI feedback) before tackling features.
3. **Keep PRs small and single-purpose** — one concern per PR to simplify review.
4. **Comment on issues before coding** to avoid duplicate work.
5. **Fix CI failures immediately** rather than merging broken code.

## Planned Contributions

### PR 1 — ElectoView (Java / Spring Boot)
- Issue: #17 — Add structured validation error responses
- Approach: Add `@RestControllerAdvice` GlobalExceptionHandler in `za.ac.cput.controller`; also remove a pre-existing duplicate handler in the test exception package that caused a bean conflict and CI failure.

### PR 2 — campus-lost-and-found (JavaScript / Jest)
- Issue: #26 — Add edge-case tests for ItemReportBuilder
- Approach: Create `Itemreportbuilder.edge.test.js` with 14 tests covering per-field required validation, empty string handling, and Director reset behaviour.

### PR 3 — university-research-collaboration-platform (Python / FastAPI)
- Feature: Add `skip`/`limit` pagination to list endpoints
- Approach: Add optional `skip` and `limit` query parameters to `GET /api/users`, `GET /api/projects`, and `GET /api/tasks` with backwards-compatible defaults.

### PR 4 — university-research-collaboration-platform (Python / FastAPI)
- Issue: #4 — Upload Research Document (US-004)
- Approach: Add `POST /api/documents/upload` endpoint using FastAPI `UploadFile`; validates file type (PDF/DOCX) and size (≤50 MB) using existing domain model constants.
