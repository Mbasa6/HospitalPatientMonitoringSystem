# Reflection — Open-Source Collaboration (Assignment 14)

## Hospital Patient Monitoring System

---

## Overview

Preparing the Hospital Patient Monitoring System for open-source collaboration required a significant shift in mindset. Throughout the earlier assignments, the repository was built with a solo development workflow in mind. Assignment 14 demanded that I consider the repository from the perspective of an external contributor encountering the codebase for the first time.

---

## Improving the Repository Based on Peer Feedback

The most immediate improvement I made was adding a `CONTRIBUTING.md` file. Before this assignment, there was no guidance on how to set up the project, what coding standards to follow, or how to submit a pull request. A contributor arriving at the repository without this context would have no clear path forward. Writing the contributing guide forced me to think through the developer experience end-to-end: cloning, building, running tests, picking an issue, and opening a PR.

I also added a `ROADMAP.md` to give potential contributors visibility into where the project is headed. This matters because contributors are more motivated when they can see a clear set of planned features they can work towards. Labelling issues as `good first issue` and `feature-request` followed from the same logic — without labels, a new contributor scanning the issue list has no way to tell which tasks are suitable for them.

The addition of a `LICENSE` file was a practical necessity. Without a licence, an open-source repository is technically not legally usable by others, even if the code is publicly visible. Choosing the MIT licence reflects the goal of making HPMS as accessible as possible for learning and contribution.

Finally, updating the README with a Getting Started section and a Features for Contribution table was the most visible improvement. The README is the first thing a contributor reads. A well-structured README communicates professionalism and lowers the barrier to entry.

---

## Challenges in Onboarding Contributors

The biggest challenge I encountered was the gap between what I understood implicitly as the sole developer and what an external contributor would need to be told explicitly. Things like the custom Maven source directory layout, the need to set `JAVA_HOME` correctly before running Maven, and the Spring Boot startup command are obvious to me after weeks of working with the project but would be confusing to someone opening it for the first time.

Another challenge was issue labelling. The existing issues were written for sprint planning, not for external contributors. Reformulating them to be approachable for a newcomer required thinking about scope and clarity differently. A good-first-issue should be small, well-defined, and not require deep knowledge of the whole system.

Branch protection also created an unexpected friction point. Enforcing PR reviews on a solo project meant I could not push directly to main, which required adjusting my workflow. In a team setting this friction is beneficial — it prevents unreviewed code from reaching production. As a solo developer it required managing owner override settings carefully.

---

## Lessons Learned About Open-Source Collaboration

The main lesson is that documentation is as much a deliverable as code. A well-tested codebase with no contributing guide is still inaccessible to new contributors. The investment in `CONTRIBUTING.md`, labelled issues, and a roadmap directly determines whether someone who finds the repository decides to contribute or moves on.

I also learned that CI/CD is foundational to open-source collaboration. The GitHub Actions workflow built in Assignment 13 means any contributor can submit a PR with confidence that their changes will be automatically validated. This removes the need for the maintainer to manually verify every submission and builds trust with contributors.

Finally, branch protection rules simulate the discipline of a professional development team. Requiring status checks before merging ensures that the main branch stays stable regardless of who is contributing. For a hospital system where software failures have real consequences, this kind of quality gate is not optional.
