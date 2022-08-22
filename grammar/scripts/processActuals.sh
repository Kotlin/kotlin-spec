#!/bin/bash

TEST_DATA="$1"
REF="$2"

for actual in `find "${TEST_DATA}" -name "*.actual"`; do
    old="${actual%.*}";
    diff "$actual" "$old" | tail -n +2 > "actual_old_diff.tmp";
    cmp -s "actual_old_diff.tmp" "$REF";

    if [[ $? == 0 ]]; then
        mv $actual $old;
    else
        echo "$actual";
        echo `diff "$actual" "$old"`;
    fi
done
