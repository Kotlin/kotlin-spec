---
name: grammar-failures
description: Investigate and categorize current grammar test failures. Shows marker summary, categorizes by syntax pattern, drills into specific categories. Use to understand the current state of grammar test coverage.
---

# Investigate Grammar Failures

Understand what tests are currently failing or marked, and why.

## Steps

1. **Summarize all marked tests** (tests passing via WITH_ERRORS, MUTE_PSI_ERRORS, MUTE):
   ```
   python3 grammar/scripts/summarizeMarkers.py
   ```

2. **List tests for a specific marker**:
   ```
   python3 grammar/scripts/summarizeMarkers.py --list WITH_ERRORS
   ```

3. **Capture and categorize actual failures** (if any tests are red):
   ```
   grammar/scripts/captureFailures.sh /tmp/grammar_failures.txt
   python3 grammar/scripts/categorizeFailures.py /tmp/grammar_failures.txt
   ```

4. **Drill into a category** — for each test in the category:
   - Read the `.kt` source to understand what syntax is involved
   - Run the individual test with `--info` to see ANTLR error details:
     ```
     ./gradlew :grammar:test --configure-on-demand --info -DTEST_PATH_FILTER="path.to.test"
     ```
   - Check the `.antlrtree.txt` golden file header for markers
   - Compare `.antlrtree.txt` vs `.antlrtree.txt.actual` if available

## Test Verdict Logic

### Diagnostics tests (`testData/diagnostics/`)
```
hasAnyErrors = lexerHasErrors || parserHasErrors
isOK = (!hasAnyErrors && !isErrorExpected) || (isErrorExpected && hasAnyErrors)
```
- No marker + no errors → pass
- `WITH_ERRORS` + has errors → pass
- No marker + has errors → **fail**
- `WITH_ERRORS` + no errors → **fail**

### PSI tests (`testData/psi/`)
```
verdictsEquals = (lexerHasErrors || parserHasErrors) == psiHasErrorElements
isOK = (verdictsEquals && !isErrorExpected)
    || (isErrorExpected && !psiHasErrorElements)
    || (psiHasErrorElements && isMutedPsiError)
```
- Both agree + no marker → pass
- `WITH_ERRORS` + PSI has no errors → pass (regardless of ANTLR)
- `MUTE_PSI_ERRORS` + PSI has errors → pass (regardless of ANTLR)

## Known Categories

The categorization scripts recognize these patterns. **New categories may emerge** as Kotlin evolves or as tests are added — when uncategorized failures cluster around a new syntax pattern, update `categorizeFailures.py` and `summarizeMarkers.py` accordingly.

| Category | Meaning |
|---|---|
| NAME_DESTRUCTURING | `val [x, y] = ...` syntax not in grammar |
| CONTEXT_PARAMS | `context(...)` edge cases |
| CONTEXT_RECEIVERS_OLD | Old `+ContextReceivers` syntax, not supported |
| WHEN_GUARDS | `is Type if expr ->` guard syntax |
| MULTI_DOLLAR | `$$"..."` multi-dollar interpolation |
| INTENTIONAL_ERRORS | Intentionally broken code, ANTLR correctly rejects |
| PSI | PSI verdict mismatch (ANTLR errors where PSI has none) |
| RECOVERY | Error recovery tests with intentionally broken syntax |
| MISSING_SOURCE | Orphan `.antlrtree.txt` with no `.kt` source |
| DANGLING | Dangling annotations without associated declarations |
| DEPRECATED_SYNTAX | Old Kotlin syntax forms (suffix `Old.kt`) |
| MULTI_FILE | Tests with `// FILE:` directives, infra limitation |
| EMPTY_ARGUMENTS | Empty/missing arguments: `foo(, 2)`, `arr[,]` |
| OTHER | Unclassified — investigate individually |
