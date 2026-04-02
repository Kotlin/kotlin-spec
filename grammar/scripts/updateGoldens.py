#!/usr/bin/env python3
"""Update golden .antlrtree.txt files from .actual files.

Usage:
  python3 updateGoldens.py failures_file.txt      — update goldens for all tests in file
  python3 updateGoldens.py path/to/test.kt ...    — update specific tests

Accepts test names with $ or / as path separator, with or without .kt suffix.
If a .actual file exists for a test, copies it over the golden file.
Does NOT touch markers — use fixMarkers.py for that.
"""
import os, sys

SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
TESTDATA = os.path.join(SCRIPT_DIR, '..', 'testData')

def update(test_name):
    """Update golden from actual for a single test. Returns True if updated."""
    test_name = test_name.replace('$', '/')
    if test_name.endswith('.kt'):
        test_name = test_name[:-3]
    actual = os.path.join(TESTDATA, test_name + '.antlrtree.txt.actual')
    golden = os.path.join(TESTDATA, test_name + '.antlrtree.txt')
    if os.path.exists(actual):
        with open(actual) as f:
            content = f.read()
        with open(golden, 'w') as f:
            f.write(content)
        print(f'Updated: {os.path.basename(golden)}')
        return True
    else:
        print(f'No .actual: {os.path.basename(test_name)}')
        return False

if len(sys.argv) < 2:
    print(__doc__)
    sys.exit(1)

arg = sys.argv[1]
if os.path.isfile(arg) and not arg.endswith('.kt'):
    # Treat as failures file
    updated = 0
    for line in open(arg):
        line = line.strip()
        if line and line.endswith('.kt'):
            if update(line):
                updated += 1
    print(f'\nUpdated {updated} golden files')
else:
    # Treat remaining args as individual test paths
    for arg in sys.argv[1:]:
        update(arg)
