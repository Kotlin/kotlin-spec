---
name: grammar-fix-tests
description: Fix grammar test data after a grammar change. Updates golden files, fixes markers, verifies all tests pass. Use after /grammar-test confirms no regressions.
---

# Fix Grammar Test Data

After a grammar change has been validated (no unexpected regressions), update the test data to make all tests green.

## Steps

1. **Update golden files** from `.actual` outputs for failing tests:
   ```
   python3 grammar/scripts/updateGoldens.py /tmp/grammar_after.txt
   ```
   Or for specific tests:
   ```
   python3 grammar/scripts/updateGoldens.py diagnostics/when/guard/whenWithGuardEnabled.kt
   ```

2. **Run tests** to check verdict mismatches:
   ```
   ./gradlew :grammar:test --configure-on-demand
   ```

3. **Fix markers** — for each remaining failure, determine the correct action:

   **Test now parses cleanly but has `WITH_ERRORS`** (grammar fix removed the error):
   ```
   python3 grammar/scripts/fixMarkers.py remove WITH_ERRORS diagnostics/path/to/test.kt
   ```

   **Test has ANTLR errors but no marker** (new test or tree was stale):
   ```
   python3 grammar/scripts/fixMarkers.py add WITH_ERRORS diagnostics/path/to/test.kt
   ```

   **PSI test: ANTLR has errors, PSI doesn't, verdicts match is needed**:
   Use `WITH_ERRORS` — this tells the test framework that ANTLR errors are expected.

   **PSI test: PSI has errors, ANTLR doesn't**:
   Use `MUTE_PSI_ERRORS` — this tells the test framework to ignore PSI errors.

4. **Verify green** — run tests again:
   ```
   ./gradlew :grammar:test --configure-on-demand
   ```
   Only the 4 infrastructure `initializationError` failures should remain.

5. **If many golden files are stale**, use `FORCE_APPLY_CHANGES` to regenerate all at once:
   - Set `FORCE_APPLY_CHANGES = true` in `grammar/src/test/.../TestRunner.kt` line 30
   - Run tests (stale files are regenerated and skipped)
   - Set `FORCE_APPLY_CHANGES = false`
   - Run tests again to verify
   - **Warning**: this only updates trees, not markers. Fix markers separately.

## Key Rules

- **Analyze each failure individually** before updating — understand whether the new tree is correct
- **Never blindly add `WITH_ERRORS`** to make a test pass — first verify the ANTLR errors are expected
- **Removing `WITH_ERRORS`** means the grammar now handles syntax it previously couldn't — confirm the parse tree is structurally correct
- After all fixes, the only remaining failures should be the 4 `initializationError` infrastructure issues
