#!/bin/env bash

source directories.sh
source ${BUILD_DIR}/prepare.sh
source settings.sh

init_settings "html"

export PROJECT_DIR
export TODO_OPTION
export STATIC_MATH_OPTION
export KATEX_BIN_OPTION

cd ${PROJECT_DIR}/src/md

TMP_DIR=${BUILD_DIR}/~tmp/html

mkdir -p $TMP_DIR

gpp -H ./index.md \
| pandoc ${PREAMBLE_OPTIONS} ${COMMON_PANDOC_OPTIONS} ${FORMAT_PANDOC_OPTIONS} -t json \
| bash ${FILTERS_DIR}/compoundFilter.sh html --split --output-directory=$TMP_DIR --generate-toc

mkdir -p ${BUILD_DIR}/html

for f in $TMP_DIR/*.json;
do \
  (pandoc \
    ${PREAMBLE_OPTIONS} \
    ${COMMON_PANDOC_OPTIONS} \
    $f \
    ${HTML_ASSETS_OPTIONS} \
    -o ${BUILD_DIR}/spec/html/"$(basename "$f" .json).html") &
done
wait

rm -rf $TMP_DIR
