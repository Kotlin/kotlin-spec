#!/bin/bash
# Capture grammar test failures to a file.
# Usage: ./captureFailures.sh [output_file]
# Default output: /tmp/grammar_failures.txt
#
# Runs :grammar:test, extracts doTest failures, filters infra noise,
# and writes sorted test names to the output file.

set -uo pipefail
OUTPUT="${1:-/tmp/grammar_failures.txt}"
cd "$(git rev-parse --show-toplevel)"

# gradlew returns non-zero when tests fail, so don't use set -e
./gradlew :grammar:test --configure-on-demand 2>&1 \
    | grep 'doTest\[.*\] FAILED' \
    | sed 's/.*doTest\[//' \
    | sed 's/\] FAILED//' \
    | sort \
    > "$OUTPUT"

COUNT=$(wc -l < "$OUTPUT" | tr -d ' ')
echo "Captured $COUNT failures to $OUTPUT"
