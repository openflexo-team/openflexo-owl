# openflexo-owl — Known defects

Bugs of openflexo-owl that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and what is fixed or
not. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `OWL-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## Loading of the W3C standard ontologies

### OWL-D-1 — Warnings logged while the standard ontologies (rdf, rdfs, owl) are loaded  ·  `DONE`

**Symptom.** Activating the OWL technology adapter logs hundreds of warnings, whatever the module that activates it (about 205 of the
583 warnings of a formod-app start). Three families, all coming from the loading of rdf / rdfs / owl:

1. `ObjectPropertyStatement.<init>`: `Ontology: ... cannot retrieve <ontology URI># for statement [..., rdfs:isDefinedBy, <ontology URI>#]`
2. `OWLConcept.updateOntologyStatements`: `Inconsistant data: unkwown property rdfs:label | rdfs:comment | rdfs:isDefinedBy`
3. `OWLDataType.<init>`: `Could not map a data type: rdfs:Class | rdfs:Resource | xsd:nonNegativeInteger, String will be used instead.`

**How it was verified.** Headless, on `owl-ta-test` (counting the three messages in the test report XML, 7 test classes, each of them
loading the standard ontologies once). Baseline before any change: 1658 / 254 / 31. Regression run through an isolated composite with
two git worktrees (original state vs. fix), JDK 8: `owl-ta-test` 33 → 40 tests (7 added), `owl-modeler` 3 → 3, `flexo-foundation-test`
521 → 521 (5 skipped both), `integration-tests` 37 → 37, `formod-module` 17 + 56 → 17 + 56, no failure. Warnings: `owl-ta-test`
1943 → 275, `owl-modeler` 410 → 0, `integration-tests` 1185 → 77. Running application (formod-app, 2026-10-09, `./gradlew
:formod:formod-app:run` from `openflexo-dev`): 0 `WARNING` line mentioning OWLOntology / OWLConcept / OWLDataType (about 205 before), 155
warnings in total instead of 583, the adapter having loaded its ontologies (checked in the log).

**Mechanism (proven).**

- *Family 1.* An ontology is not an `OWLConcept` (not a class, an individual nor a property), so `OWLOntology.retrieveOntologyObject`
  cannot return it: the object of `rdfs:isDefinedBy` (the ontology itself, written with a trailing `#`) falls through every lookup and
  returns `null` without reaching the "Unexpected resource" branch.
- *Family 2.* rdf and rdfs import each other. While one of them is being loaded, the other computes the statements of its concepts
  (`OWLClass.init`) before `rdfs:label`, `rdfs:comment`, `rdfs:isDefinedBy` exist. The warning is transient: `OWLOntologyLibrary.init`
  computes the statements again once all three are loaded, and after that no warning is produced (test).
- *Family 3, rdfs:Class and rdfs:Resource.* Same cycle: a `RangeStatement` does not find the class yet and falls back on a data type,
  which also left bogus entries in `OWLOntologyLibrary.getDataTypes()`.
- *Family 3, XSD types.* Genuinely unmapped: `BuiltInDataType` (openflexo-core) had no entry for them.

**Fixed.**

- Family 1: `OWLOntology.isOntologyReference(Resource)` (this ontology or any ontology known by the library, URI with or without `#`);
  `ObjectPropertyStatement` no longer warns for it, `statementObject` stays `null` as before.
- Families 2 and 3 (rdfs): `OWLOntologyLibrary.isStandardTermBeingBootstrapped(uri)`; `OWLConcept.updateOntologyStatements` logs at
  `FINE` and `RangeStatement` creates no data type for a standard term while the standard ontologies are bootstrapping.
- Family 3 (XSD): `BuiltInDataType` gets `NonNegativeInteger`, `PositiveInteger` (Integer), `AnyURI` (String), `Date`, `DateTime`
  (`java.util.Date`), with their URIs in `W3URIDefinitions`; `OWLDataType.valueFromLiteral` reads them. Behaviour change: a
  `xsd:nonNegativeInteger` literal used to be read as a `String`, it is now an `Integer`.
- Tests: `TestStandardOntologyLoadingWarnings` (`owl-ta-test`). It keeps the legitimate case: a reference to an unknown resource still
  warns.

**Not fixed (outside the standard ontologies, not investigated beyond counting).**

- `cannot retrieve` on user ontologies (258 → 256 after the 2 expected from the guard-rail test): BPMN 220, cpmf 33, skos 3. Includes
  `owl:unionOf` towards anonymous nodes and `rdfs:seeAlso` towards a non-ontology URI. Whether each is legitimate is not established.
- 14 `unkwown property` on `dc/terms:*` (Dublin Core terms used without declaration): kept as a warning, it is dangling data.
- 3 `Could not map a data type` for the ranges of `Pizza`, `PizzaBase`, `PizzaTopping` in a user ontology (probably the same loading
  cycle, on a user ontology).
- 74 `unkwown property` in `integration-tests`: `owl:dataPropertyRange` (24), `owl:dataPropertyDomain` (20), `owl:objectPropertyRange`
  (16), plus `dc/terms`. Not produced during the bootstrap, so not covered by the fix.

**Scope.** `DONE` for the standard ontologies (rdf, rdfs, owl, XSD). The items under "Not fixed" concern user ontologies and are not
covered by this entry; open a new `OWL-D-<n>` if one of them is taken on.
