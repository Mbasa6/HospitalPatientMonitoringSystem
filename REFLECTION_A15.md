# Reflection — Assignment 15: Cross-Project Contributions

## Overview

Contributing to three peer repositories taught me that open-source collaboration requires far more than writing correct code. It demands understanding unfamiliar codebases quickly, respecting existing conventions, and communicating clearly through commit messages, PR descriptions, and issue references.

## What I Learned

### Reading before writing
Before making any change, I had to understand each project's architecture, testing framework, and coding style. ElectoView used Java with Spring Boot and Maven; campus-lost-and-found used Node.js with Jest; and the university-research-collaboration-platform used Python with FastAPI and pytest. Each required a different mental model before a single line of code could be written.

### CI is not optional
Every PR had to pass CI before it could be merged. On ElectoView, the initial PR failed because the branch included unrelated files and a duplicate `@RestControllerAdvice` bean that prevented the Spring ApplicationContext from loading. On the university-research-collaboration-platform, the document upload PR failed because `python-multipart` was missing from the CI install step — a dependency the workflow had never needed before. Diagnosing and fixing these failures taught me to always verify the full diff before pushing and to check whether new dependencies are reflected in the CI configuration, not just in `requirements.txt`.

### Small, focused PRs get merged faster
The campus-lost-and-found PR (14 edge-case Jest tests, one new file) was the smoothest contribution — a single file, a clear purpose, and zero risk of breaking existing behaviour. Larger PRs require more context from reviewers and are more likely to attract revision requests.

### Feature contributions have higher impact
Adding the document upload endpoint and pagination to the university-research-collaboration-platform required understanding the existing service, repository, and domain layers before extending them. These contributions added real user-facing functionality rather than just fixing existing behaviour, which made them more rewarding to deliver.

## Challenges

**Dirty git branches:** On ElectoView, the first PR accidentally included over 100 unrelated files because the feature branch was created from the fork's main rather than from upstream/main. The fix was to create a clean branch directly from upstream/main and cherry-pick only the intended file.

**Pre-existing CI failures:** Some CI failures were unrelated to my changes — for example, the ElectoView integration tests required a live MySQL database that was never configured in the CI environment. Understanding the difference between a failure I introduced and a pre-existing one required reading the full error logs carefully.

**Missing dependencies in CI:** The python-multipart issue illustrated that adding a dependency to requirements.txt is not enough if the CI workflow installs packages explicitly via pip install rather than via pip install -r requirements.txt. Both files needed to be updated.

## Conclusion

Assignment 15 gave me practical experience with the full open-source contribution workflow: forking, branching, coding, testing, pushing, and responding to CI feedback. The skills developed here — reading unfamiliar codebases, writing focused PRs, and fixing CI failures — are directly transferable to real-world software development.
