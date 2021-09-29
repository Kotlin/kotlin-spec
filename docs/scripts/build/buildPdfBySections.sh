#!/bin/env bash

source directories.sh
source ${BUILD_DIR}/prepare.sh
source settings.sh

init_settings "pdf"

export PROJECT_DIR
export TODO_OPTION
export STATIC_MATH_OPTION

cd ${PROJECT_DIR}/src/md

TMP_DIR=${BUILD_DIR}/~tmp/pdf

export IMAGE_DIR_OPTION="--image-directory=${TMP_DIR}/pdf/images"

mkdir -p $TMP_DIR

gpp -H ./index.md \
| pandoc ${PREAMBLE_OPTIONS} ${COMMON_PANDOC_OPTIONS} ${FORMAT_PANDOC_OPTIONS} -t json \
| bash ${FILTERS_DIR}/compoundFilter.sh latex --split --output-directory=$TMP_DIR

mkdir -p ${BUILD_DIR}/pdf/sections

for f in $TMP_DIR/*.json;
do \
  (pandoc \
    ${PREAMBLE_OPTIONS} \
    ${COMMON_PANDOC_OPTIONS} \
    $f \
    --variable documentclass=book \
    --number-sections \
    -o ${BUILD_DIR}/spec/pdf/sections/"$(basename "$f" .json).pdf") &
done
wait

rm -rf $TMP_DIR
