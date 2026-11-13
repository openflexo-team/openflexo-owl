# openflexo-owl — Known defects

Bugs of openflexo-owl that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and what is fixed or
not. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `OWL-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

Fixed defects are moved to [`FIXED_DEFECTS.md`](./FIXED_DEFECTS.md) once their status is `DONE`.

---

## User-interface tests

### OWL-D-2 — `TestFIBOWLClassSelector.selectRootClass` fails when a display is available  ·  `TODO`

**Symptom.** In `owl-ta-ui`, once the UI tests really run (with a display), 47 of the 48 tests of the module pass and
`TestFIBOWLClassSelector.selectRootClass` fails on its second assertion (line 193):
`assertEquals(obm.getRoots().get(0), collection)`. After `selector.setRootClass(collection)` the browser model holds one root (line 192
passes), but it is not `collection`.

**What is verified.** Seen on the Jenkins job `openflexo-owl-release` build #1 (2026-10-16), whose release job had an Xvnc display.
Before that date the daily job `openflexo-owl-2.99` had no display: every UI test of the module failed in `classMethod` with
`HeadlessException` (16 tests), the build staying blue because the build plugin ignores test failures. Not reproduced locally, not
investigated: whether the code or the test is at fault is unknown.

**To reproduce.** With a display, from the `openflexo-dev` composite:
`./gradlew :openflexo-owl:owl-ta-ui:cleanUiTest :openflexo-owl:owl-ta-ui:uiTest --tests '*TestFIBOWLClassSelector'`.

**Consequence for the 2.99 release.** The release job `openflexo-owl-release` runs the `test` task, so the UI tests of this module were not
executed for the release (tracked as `JENKINS-D-6` in the Jenkins backlog).
