---
name: grammar-test
description: Build and test ANTLR grammar changes. Captures baseline, rebuilds, runs tests, diffs failures to find regressions and fixes. Use after editing KotlinLexer.g4 or KotlinParser.g4.
---

# Grammar Test Workflow

Validate a grammar change by comparing test results before and after.

## Steps

1. **Capture baseline** before making changes (skip if baseline already captured this session):
   ```
   grammar/scripts/captureFailures.sh /tmp/grammar_baseline.txt
   ```

2. **Edit the grammar** — make changes to `grammar/src/main/antlr/KotlinLexer.g4` and/or `KotlinParser.g4`.

3. **Build** the grammar:
   ```
   ./gradlew :grammar:clean :grammar:jar --configure-on-demand
   ```
   Always use `:grammar:clean` — generated Java files are NOT cleaned otherwise.

4. **Run tests** and capture new failures:
   ```
   grammar/scripts/captureFailures.sh /tmp/grammar_after.txt
   ```

5. **Diff** baseline vs new:
   ```
   grammar/scripts/diffFailures.sh /tmp/grammar_baseline.txt /tmp/grammar_after.txt
   ```

6. **Analyze results**:
   - **0 new regressions**: good — proceed to fix test data with `/grammar-fix-tests`
   - **New regressions found**: analyze each one individually (see below)

## Analyzing a Regression

For each new regression, run the individual test with `--info` to get error details:
```
./gradlew :grammar:test --configure-on-demand --info -DTEST_PATH_FILTER="path.to.test"
```

Then read the `.antlrtree.txt.actual` file and compare to the golden `.antlrtree.txt`:
```
diff grammar/testData/<path>.antlrtree.txt grammar/testData/<path>.antlrtree.txt.actual
```

Determine which case applies:

1. **Grammar change fixed the parse, golden file is stale** — The new parse tree is correct. The `.antlrtree.txt` was generated with the old grammar. Fix with `/grammar-fix-tests`.

2. **Grammar change broke a previously correct parse** — The old golden file was correct, and the new parse tree is wrong. Fix the grammar rule.

3. **Golden file was already wrong/stale before your change** — Verify by checking it was in baseline failures. Not caused by your change.

4. **Verdict mismatch (PSI tests only)** — ANTLR error/no-error verdict differs from PSI verdict. Consider `WITH_ERRORS` or `MUTE_PSI_ERRORS` markers.

## Key Rules

- **Always use `--configure-on-demand`** to skip `:docs` module (maven.apal-research.com is frequently unreachable)
- **Analyze each failing test individually** — never batch-apply changes without understanding what changed
- **Never trust a golden file blindly** — it may reflect a parse that was already broken
- The `.antlrtree.txt.actual` file is your primary diagnostic tool
