source directories.sh
source ${BUILD_DIR}/prepare.sh
source settings.sh

init_settings "html"

export PROJECT_DIR
export TODO_OPTION
export STATIC_MATH_OPTION
export KATEX_BIN_OPTION

cd ${PROJECT_DIR}/src/md

gpp -H ./index.md | pandoc \
--filter ${FILTERS_DIR}/compoundFilter.sh \
  ${PREAMBLE_OPTIONS} \
  ${COMMON_PANDOC_OPTIONS} \
  ${FORMAT_PANDOC_OPTIONS} \
  ${TOC_PANDOC_OPTIONS} \
  ${HTML_ASSETS_OPTIONS} \
-o ${BUILD_DIR}/spec/html/kotlin-spec.html
