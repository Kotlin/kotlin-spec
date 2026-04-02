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

## Handling Test Failures

When a grammar change causes test failures, analyze each failure individually. Never batch-apply changes without understanding what changed and why.

### Running an Individual Test

```bash
# Use TEST_PATH_FILTER system property (regex matched against file path)
./gradlew :grammar:test --configure-on-demand -DTEST_PATH_FILTER="diagnostics\$when\$guard\$whenWithGuardEnabled\.kt"

# Broader filter for a directory
./gradlew :grammar:test --configure-on-demand -DTEST_PATH_FILTER="diagnostics.when.guard"
```

### For Each Failing Test, Determine Which Case Applies

1. **Grammar change fixed the parse, golden file is stale** — The `.antlrtree.txt` was generated with the old grammar. The new parse tree is correct. Compare the `.antlrtree.txt.actual` against the golden file to confirm the tree structure is now correct (e.g., tokens that were previously flat/broken are now properly nested under the right rule nodes). Regenerate the golden file.

2. **Grammar change broke a previously correct parse (regression)** — The old golden file was correct, and the new parse tree is wrong. This means the grammar change introduced an ambiguity or incorrect match. Fix the grammar rule.

3. **Golden file was already wrong/stale before your change** — The test was already failing at baseline. Your change may have changed *how* it fails but didn't cause the failure. Verify by checking baseline failures. Don't fix these unless they're in scope.

4. **Verdict mismatch (PSI tests only)** — The ANTLR parse tree is correct, but the error/no-error verdict differs from the PSI verdict. This can happen when ANTLR error recovery differs from Kotlin's PSI parser. Consider `WITH_ERRORS` or `MUTE_PSI_ERRORS` markers if appropriate.

The **`.antlrtree.txt.actual` file** is your primary diagnostic tool. Failed tests dump the actual parse output next to the expected file. Read it, compare it to the golden file, and decide which case above applies. Use `scripts/compareActuals.sh testData/` to walk through diffs visually (requires `meld`).

**Never trust a golden file blindly** — it may have been generated from a previous grammar version, from stale source code, or may reflect a parse that was already broken. Always verify the tree structure makes sense for the source code.

## Analyzing Test Results

- After a grammar change: run tests, compare failure count against baseline
- New regressions = tests that passed before but fail now (must be investigated)
- Newly fixed tests = tests that failed before but now the `.antlrtree.txt` is stale (regenerate with `FORCE_APPLY_CHANGES`)
- Use `scripts/compareActuals.sh testData/` to visually diff `.actual` files against expected
- Use `scripts/processActuals.sh testData/ ref` to batch-apply actuals that match a known diff pattern

## Utility Scripts

### Capture & Compare Failures

```bash
# Capture current test failures to a file
scripts/captureFailures.sh /tmp/baseline.txt

# After a grammar change, capture again and compare
scripts/captureFailures.sh /tmp/after_change.txt
scripts/diffFailures.sh /tmp/baseline.txt /tmp/after_change.txt
```

### Categorize & Summarize

```bash
# Categorize failures by source code patterns (name destructuring, context params, etc.)
python3 scripts/categorizeFailures.py /tmp/baseline.txt

# Summarize all tests with WITH_ERRORS / MUTE_PSI_ERRORS / MUTE markers
python3 scripts/summarizeMarkers.py

# List all tests with a specific marker
python3 scripts/summarizeMarkers.py --list WITH_ERRORS
```

### Update Golden Files & Fix Markers

```bash
# Update golden files from .actual for all tests in a failures file
python3 scripts/updateGoldens.py /tmp/failures.txt

# Update golden for specific tests
python3 scripts/updateGoldens.py diagnostics/when/guard/whenWithGuardEnabled.kt

# Add WITH_ERRORS marker to tests in a failures file
python3 scripts/fixMarkers.py add WITH_ERRORS /tmp/failures.txt

# Remove WITH_ERRORS from specific tests that now parse cleanly
python3 scripts/fixMarkers.py remove WITH_ERRORS diagnostics/foo.kt diagnostics/bar.kt
```
