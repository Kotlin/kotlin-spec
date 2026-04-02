# Grammar Subproject

ANTLR v4 grammar for the Kotlin language, tested against the Kotlin compiler's PSI and diagnostics test suites.

## Build & Test Workflow

```bash
# Quick smoke test (builds parser JAR only)
./gradlew :grammar:jar --configure-on-demand

# Full test cycle
./gradlew :grammar:downloadCompilerTests
./gradlew :grammar:prepareDiagnosticsCompilerTests
./gradlew :grammar:syncWithCompilerTests   # removes orphan .antlrtree.txt files
./gradlew :grammar:test --configure-on-demand

# Always use --configure-on-demand to skip :docs module dependencies
# (maven.apal-research.com is frequently unreachable)
```

## Editing the Grammar

- Grammar source: `src/main/antlr/KotlinLexer.g4` and `KotlinParser.g4`
- After editing, always use `:grammar:clean` before `:grammar:jar` — generated Java files in `src/main/java/.../parser/` are NOT cleaned otherwise
- ANTLR version is 4.8 (pinned in `build.gradle.kts`)

## Test Infrastructure

- Tests compare ANTLR parse output against `.antlrtree.txt` expected (golden) files
- Two test categories:
  - **PSI tests** (`testData/psi/`): have a `.txt` PSI tree file; test passes if ANTLR error verdict matches PSI error verdict
  - **Diagnostics tests** (`testData/diagnostics/`): no PSI tree; test passes if ANTLR parse is error-free (unless `WITH_ERRORS` marker)
- `.antlrtree.txt` header format: `File: name.kt - <md5hash> [(WITH_ERRORS|MUTE|MUTE_PSI_ERRORS)]`
- To regenerate `.antlrtree.txt` files: set `FORCE_APPLY_CHANGES = true` in `TestRunner.kt` line 30, run tests, then set it back to `false`
- Failed tests dump `.antlrtree.txt.actual` files for comparison
- Filter tests with system property: `-DTEST_PATH_FILTER="regex"`

### Test Markers

- `WITH_ERRORS` — parser errors are expected (test passes if errors exist)
- `MUTE` — test is skipped entirely
- `MUTE_PSI_ERRORS` — PSI errors are ignored for verdict comparison

## Skills

Use these slash commands for grammar workflows:
- `/grammar-test` — validate a grammar change (baseline, build, test, diff)
- `/grammar-failures` — investigate and categorize current test failures
- `/grammar-fix-tests` — fix test data after a grammar change (update goldens, fix markers)

## Utility Scripts

| Script | Purpose |
|---|---|
| `scripts/captureFailures.sh` | Run tests, save failure list to file |
| `scripts/diffFailures.sh` | Compare two failure lists → regressions + fixes |
| `scripts/categorizeFailures.py` | Categorize failures by source patterns |
| `scripts/updateGoldens.py` | Copy `.actual` → golden for specified tests |
| `scripts/fixMarkers.py` | Add/remove `WITH_ERRORS`/`MUTE_PSI_ERRORS` markers |
| `scripts/summarizeMarkers.py` | Count and categorize all marked tests |
