COMMON_PANDOC_OPTIONS="\
  -s \
  --variable linkcolor=blue \
  --variable filecolor=cyan \
  -f markdown-raw_html+smart+tex_math_double_backslash"

TOC_PANDOC_OPTIONS="\
  --toc \
  --toc-depth=6"

HTML_ASSETS_OPTIONS="\
  -c ./${ASSETS_DIRECTORY}/css/main.css \
  --katex=./${ASSETS_DIRECTORY}/js/katex/"

PREAMBLE_OPTIONS=""

init_settings() {
    local type=$1
    local mode=$2
    if [ "${type}" == "pdf" ]; then
        PREAMBLE_OPTIONS="-H ./preamble.tex"
    elif [ "${type}" == "html" ]; then
        PREAMBLE_OPTIONS="-H ./preamble.md"
    fi

    if [ "${mode}" == "section" ]; then
        HTML_ASSETS_OPTIONS="\
            -c ../${ASSETS_DIRECTORY}/css/main.css \
            --katex=../${ASSETS_DIRECTORY}/js/katex/"
    fi
}
