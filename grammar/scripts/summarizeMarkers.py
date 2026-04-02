#!/usr/bin/env python3
"""Summarize all tests with WITH_ERRORS, MUTE_PSI_ERRORS, or MUTE markers.

Usage: python3 summarizeMarkers.py [--list MARKER]

Without --list: prints category counts for each marker type.
With --list: prints all test paths for the given marker.
"""
import os, re, sys

SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
TESTDATA = os.path.join(SCRIPT_DIR, '..', 'testData')
MARKERS = ('WITH_ERRORS', 'MUTE_PSI_ERRORS', 'MUTE')

marked = {m: [] for m in MARKERS}

for root, dirs, files in os.walk(TESTDATA):
    for f in files:
        if f.endswith('.antlrtree.txt') and not f.endswith('.actual'):
            path = os.path.join(root, f)
            first = open(path).readline().strip()
            for m in MARKERS:
                if f'({m})' in first:
                    rel = os.path.relpath(path, TESTDATA).replace('.antlrtree.txt', '.kt')
                    marked[m].append(rel)

# Handle --list mode
if len(sys.argv) >= 3 and sys.argv[1] == '--list':
    target = sys.argv[2]
    if target not in MARKERS:
        print(f'Unknown marker: {target}. Must be one of {MARKERS}', file=sys.stderr)
        sys.exit(1)
    for f in sorted(marked[target]):
        print(f)
    sys.exit(0)

# Summary mode
def categorize(f, content):
    """Categorize a test by source code patterns."""
    if re.search(r'(val|var)\s*\[', content) or re.search(r'for\s*\(\s*\[', content) \
       or re.search(r'\{\s*\[', content) or re.search(r'\(val\s+\w+\s*=', content) \
       or 'namebaseddestructuring' in f.lower():
        return 'NAME_DESTRUCTURING'
    if 'guard' in f.lower():
        return 'WHEN_GUARDS'
    if re.search(r'context\s*\(', content) and ('contextReceivers' in f or 'OnClass' in f):
        return 'CONTEXT_RECEIVERS_OLD'
    if re.search(r'context\s*\(', content):
        return 'CONTEXT_PARAMS'
    if 'MultiDollar' in f:
        return 'MULTI_DOLLAR'
    if f.startswith('psi/'):
        return 'PSI'
    if 'incompleteCode' in f or 'exceptions/' in f or 'CrashInRedCode' in f \
       or 'SyntaxError' in f or 'EmptyThrow' in f:
        return 'INTENTIONAL_ERRORS'
    if f.endswith('Old.kt'):
        return 'DEPRECATED_SYNTAX'
    if '// FILE:' in content:
        return 'MULTI_FILE'
    if 'Dangling' in f or 'dangling' in f:
        return 'DANGLING'
    if 'recovery/' in f or 'regressions/' in f:
        return 'RECOVERY'
    return 'OTHER'

for m in MARKERS:
    items = sorted(marked[m])
    if not items:
        continue

    cats = {}
    for f in items:
        kt = os.path.join(TESTDATA, f)
        if not os.path.exists(kt):
            cats.setdefault('MISSING_SOURCE', []).append(f)
            continue
        content = open(kt).read()
        cat = categorize(f, content)
        cats.setdefault(cat, []).append(f)

    print(f'=== {m}: {len(items)} ===')
    for cat, citems in sorted(cats.items(), key=lambda x: -len(x[1])):
        print(f'  {cat}: {len(citems)}')
    print()

total = sum(len(v) for v in marked.values())
print(f'TOTAL MARKED: {total}')
