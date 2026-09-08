#!/bin/env bash

FORMAT_PANDOC_OPTIONS="-f markdown-raw_html+smart+tex_math_double_backslash"

#--variable=subparagraph is a fix for https://stackoverflow.com/questions/42916124/not-able-to-use-titlesec-with-markdown-and-pandoc

COMMON_PANDOC_OPTIONS="\
  -s \
  --variable linkcolor=blue \
  --variable filecolor=cyan \
  --syntax-definition=kotlin.xml \
  --variable=subparagraph \
  --variable=math: \
  --top-level-division=part"

TOC_PANDOC_OPTIONS="\
  --toc \
  --toc-depth=6"

HTML_ASSETS_OPTIONS="\
  -c ./resources/css/main.css \
  -c ./resources/js/katex/katex.min.css \
  --katex=./resources/js/katex/"

PREAMBLE_OPTIONS=""

init_settings() {
    local type=$1
    if [ "${type}" == "pdf" ]; then
        PREAMBLE_OPTIONS="-H ./preamble.tex"
    elif [ "${type}" == "html" ]; then
      PREAMBLE_OPTIONS="-H ./preamble.html --include-before-body ./preface.html --include-after-body ./epilogue.html"
      if [ "${STATIC_MATH_OPTION}" == "--disable-static-math" ]; then
        PREAMBLE_OPTIONS="$PREAMBLE_OPTIONS -H ./dynamic_math.html"
      fi
    fi
}
