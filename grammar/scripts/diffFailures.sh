#!/bin/bash
# Compare two failure lists to find regressions and fixes.
# Usage: ./diffFailures.sh <baseline_file> <new_file>
#
# Both files must contain sorted test names (one per line).
# captureFailures.sh produces sorted output.
#
# Output:
#   NEW REGRESSIONS — tests that pass in baseline but fail now
#   FIXED — tests that fail in baseline but pass now

set -euo pipefail

if [ $# -lt 2 ]; then
    echo "Usage: $0 <baseline_file> <new_file>" >&2
    exit 1
fi

BASELINE="$1"
NEW="$2"

echo "=== NEW REGRESSIONS ($(comm -13 "$BASELINE" "$NEW" | wc -l | tr -d ' ')) ==="
comm -13 "$BASELINE" "$NEW"
echo
echo "=== FIXED ($(comm -23 "$BASELINE" "$NEW" | wc -l | tr -d ' ')) ==="
comm -23 "$BASELINE" "$NEW"
