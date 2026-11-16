# openflexo-owl — Backlog

Features and evolutions to develop. Each entry says what problem it solves and when it is done. Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `OWL-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### OWL-F-1 — The owl adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`), done with the other remaining adapters in one batch (asked by the user, 2026-11-16), with the rules settled on the first adapters (xlsx, xml, jdbc, rest, formod, docx): the edition actions caught the failures of their evaluations (four catches printing the stack, or a log), and went on with null after a warning.

**Rules applied (decided with the user on the first adapters).** Evaluations through `FMLEvaluation`; every `execute` / `performExecute` declares `throws FMLExecutionException`; a null receiver, model, document, name or required input is an `FMLNullReferenceError`; a select from a null source is an `FMLNullReferenceError` naming the select; a foreseen failure of the technology an `FMLTechnologyError` with a message of its own; a `ModelDefinitionException` an `FMLInternalError`; a cancelled loading an `FMLCancellation`; a typed failure of `AbstractCreateResource` rethrown unchanged; a log just before a throw removed (the failure is reported once, by the editor); the `System.out` traces of the actions removed. Scope: the paths that FML execution goes through; loading and conversion code left.

**Done (2026-11-16).**
- `AddOWLClass`, `AddOWLIndividual`, `AddOWLDataProperty`: no ontology is an `FMLNullReferenceError`, a duplicate URI an `FMLTechnologyError` (it was a printed `DuplicateURIException` and null).
- `AddDataPropertyStatement`, `AddObjectPropertyStatement`: no property is an `FMLResolutionError`, a null subject, value or object an `FMLNullReferenceError` (they returned null); `AddRestrictionStatement`: no ontology an `FMLNullReferenceError`, a subject or object that is not a class, or no property, an `FMLTypeError` (it returned null).
- `CreateOWLResource`: typed failures, rethrown unchanged when already typed.
- The selects of classes, individuals and object properties: a null ontology is an `FMLNullReferenceError`.
- The conditions and values of the ontology assertions are strict since `XML-F-1` (core, `flexo-ontology`).

**Known limits.**
- `AddOWLIndividual` creates the individual before evaluating its assertions: an assertion that now fails leaves the individual in the ontology (no simple way to delete it).

**After review (batch review).** `AddTypeStatement`, `AddSubClassStatement`: a null subject, type or father is an `FMLNullReferenceError`
(an NPE, or a statement on null); the subject of the statements (`AddStatement`), the restriction type and the cardinality evaluate
through `FMLEvaluation` (an unset restriction type is still `Some`, an unset cardinality 1).
Chosen without an explicit decision: a statement whose value or object is null, and a restriction between things that are not classes,
are errors (they returned null).

**Verified (2026-11-16).** owl-ta-test 40 green through the openflexo-dev composite; ECJ compiles owl-ta. No new test (owl has no fml-cli test infrastructure).
