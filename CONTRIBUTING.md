# Contributing to Hospital Patient Monitoring System

Thank you for your interest in contributing to HPMS. This document outlines how to get started, the coding standards we follow, and how to submit changes.

---

## Prerequisites

- Java 17 (Temurin recommended)
- Maven 3.8+
- Git

---

## Setup Instructions

1. Fork this repository on GitHub.
2. Clone your fork:
   ```bash
   git clone https://github.com/<your-username>/HospitalPatientMonitoringSystem.git
   cd HospitalPatientMonitoringSystem
   ```
3. Verify the build and tests pass:
   ```bash
   mvn clean test
   ```
4. Run the API locally:
   ```bash
   mvn spring-boot:run
   ```
5. Open the interactive API docs at:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---

## Coding Standards

- Follow standard Java naming conventions (camelCase for methods/variables, PascalCase for classes).
- All new features must include at least one unit test in `tests/com/hpms/`.
- Tests must pass before submitting a PR: `mvn clean test`
- Keep commits small and focused on a single concern.
- Write clear, present-tense commit messages: `Add alert threshold validation`, not `Added stuff`.

---

## Picking Issues

1. Browse [open issues](https://github.com/Mbasa6/HospitalPatientMonitoringSystem/issues).
2. Look for issues labelled `good first issue` for beginner-friendly tasks.
3. Look for issues labelled `feature-request` for larger enhancements.
4. Comment on the issue to let others know you are working on it.

---

## Submitting a Pull Request

1. Create a feature branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Make your changes and write tests.
3. Ensure all tests pass:
   ```bash
   mvn clean test
   ```
4. Push your branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a Pull Request on GitHub targeting `main`.
6. Fill in the PR description with:
   - What the change does
   - Which issue it closes (e.g. `Closes #12`)
   - How to test it

---

## CI/CD

All PRs are automatically tested by GitHub Actions. The `Build and Test` check must pass before a PR can be merged. Do not open a PR with known failing tests.
