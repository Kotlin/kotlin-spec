#!/usr/bin/env python3
"""Categorize grammar test failures by source code patterns.

Usage: python3 categorizeFailures.py [failures_file]
Default input: /tmp/grammar_failures.txt

Reads a list of failing test names (one per line, $ or / as path separator)
and categorizes them by examining the .kt source files.
"""
import os, re, sys

SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
TESTDATA = os.path.join(SCRIPT_DIR, '..', 'testData')

failures_file = sys.argv[1] if len(sys.argv) > 1 else '/tmp/grammar_failures.txt'

failures = []
for line in open(failures_file):
    line = line.strip()
    if line and line.endswith('.kt'):
        # Normalize to / separators
        failures.append(line.replace('$', '/'))

cats = {}
for f in failures:
    path = os.path.join(TESTDATA, f)
    if not os.path.exists(path):
        cats.setdefault('MISSING_SOURCE', []).append(f)
        continue
    content = open(path).read()
    cat = None

    # Name-based destructuring
    if re.search(r'(val|var)\s*\[', content) or re.search(r'for\s*\(\s*\[', content) \
       or re.search(r'\{\s*\[', content) or re.search(r'\(val\s+\w+\s*=', content) \
       or 'namebaseddestructuring' in f.lower():
        cat = 'NAME_DESTRUCTURING'
    # When guards
    elif 'when' in content and (
        re.search(r'^\s+(is|in)\s+.*\bif\b', content, re.MULTILINE) or
        re.search(r'^\s+else\s+if\b', content, re.MULTILINE) or
        'guard' in f.lower()):
        cat = 'WHEN_GUARDS'
    # Context parameters
    elif re.search(r'context\s*\(', content):
        if 'contextReceivers' in f or 'OnClass' in f:
            cat = 'CONTEXT_RECEIVERS_OLD'
        else:
            cat = 'CONTEXT_PARAMS'
    # Multi-dollar interpolation
    elif re.search(r'\$\$["\']', content) or re.search(r'\$\$\{', content) or 'MultiDollar' in f:
        cat = 'MULTI_DOLLAR'
    # Multi-file tests
    elif '// FILE:' in content:
        cat = 'MULTI_FILE'
    # Intentional errors / incomplete code
    elif 'incompleteCode' in f or 'exceptions/' in f or 'CrashInRedCode' in f \
         or 'SyntaxError' in f or 'EmptyThrow' in f:
        cat = 'INTENTIONAL_ERRORS'
    # Deprecated syntax
    elif f.endswith('Old.kt'):
        cat = 'DEPRECATED_SYNTAX'
    # Empty arguments
    elif 'emptyArgument' in f or 'emptyNamedAndSpread' in f or 'missingNames' in f:
        cat = 'EMPTY_ARGUMENTS'
    # Dangling annotations
    elif 'Dangling' in f or 'dangling' in f:
        cat = 'DANGLING_ANNOTATION'
    # PSI tests (separate from diagnostics)
    elif f.startswith('psi/'):
        cat = 'PSI'
    # Recovery tests
    elif 'recovery/' in f or 'regressions/' in f:
        cat = 'RECOVERY'
    else:
        cat = 'OTHER'

    cats.setdefault(cat, []).append(f)

total = 0
for cat, items in sorted(cats.items(), key=lambda x: -len(x[1])):
    total += len(items)
    print(f'{cat}: {len(items)}')
    for i in items[:5]:
        print(f'  {i}')
    if len(items) > 5:
        print(f'  ... +{len(items)-5}')
    print()
print(f'TOTAL: {total}')
