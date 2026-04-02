#!/usr/bin/env python3
"""Add or remove WITH_ERRORS/MUTE_PSI_ERRORS markers on golden files.

Usage:
  python3 fixMarkers.py add    WITH_ERRORS      test1.kt test2.kt ...
  python3 fixMarkers.py remove WITH_ERRORS      test1.kt test2.kt ...
  python3 fixMarkers.py add    MUTE_PSI_ERRORS  test1.kt test2.kt ...
  python3 fixMarkers.py add    WITH_ERRORS      failures_file.txt
  python3 fixMarkers.py remove WITH_ERRORS      failures_file.txt

Accepts test names with $ or / as path separator, with or without .kt suffix.
When given a file (not ending in .kt), reads test names one per line.
"""
import os, sys

SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
TESTDATA = os.path.join(SCRIPT_DIR, '..', 'testData')
VALID_MARKERS = ('WITH_ERRORS', 'MUTE_PSI_ERRORS', 'MUTE')

def fix_marker(test_name, action, marker):
    test_name = test_name.replace('$', '/')
    if test_name.endswith('.kt'):
        test_name = test_name[:-3]
    golden = os.path.join(TESTDATA, test_name + '.antlrtree.txt')
    if not os.path.exists(golden):
        return False

    content = open(golden).read()
    first_line = content.split('\n')[0]
    rest = content[len(first_line):]

    if action == 'add':
        if f'({marker})' in first_line:
            return False  # already has it
        # Remove any existing marker first
        for m in VALID_MARKERS:
            first_line = first_line.replace(f' ({m})', '')
        first_line = first_line + f' ({marker})'
    elif action == 'remove':
        if f'({marker})' not in first_line:
            return False  # doesn't have it
        first_line = first_line.replace(f' ({marker})', '')
    else:
        print(f'Unknown action: {action}', file=sys.stderr)
        return False

    with open(golden, 'w') as f:
        f.write(first_line + rest)
    return True

if len(sys.argv) < 4:
    print(__doc__)
    sys.exit(1)

action = sys.argv[1]
marker = sys.argv[2]
if marker not in VALID_MARKERS:
    print(f'Invalid marker: {marker}. Must be one of {VALID_MARKERS}', file=sys.stderr)
    sys.exit(1)

targets = sys.argv[3:]
count = 0

for arg in targets:
    if os.path.isfile(arg) and not arg.endswith('.kt'):
        # Treat as failures file
        for line in open(arg):
            line = line.strip()
            if line and line.endswith('.kt'):
                if fix_marker(line, action, marker):
                    count += 1
    else:
        if fix_marker(arg, action, marker):
            count += 1

verb = 'Added' if action == 'add' else 'Removed'
print(f'{verb} {marker} on {count} files')
