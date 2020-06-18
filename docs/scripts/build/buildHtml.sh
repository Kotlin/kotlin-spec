source directories.sh
source settings.sh

init_settings "html"

export PROJECT_DIR
export TODO_OPTION
export STATIC_MATH_OPTION

cd ${PROJECT_DIR}/src/md

gpp -H ./index.md | pandoc \
--filter ${FILTERS_DIR}/processTodoFilter.sh \
--filter ${FILTERS_DIR}/markSentencesFilter.sh \
--filter ${FILTERS_DIR}/copyPasteFilter.sh \
--filter ${FILTERS_DIR}/inlineDiagramFilter.sh \
--filter ${FILTERS_DIR}/mathInCodeFilter.sh \
--filter ${FILTERS_DIR}/brokenReferencesReportFilter.sh \
--filter ${FILTERS_DIR}/inlineKatexFilter.sh \
  ${PREAMBLE_OPTIONS} \
  ${COMMON_PANDOC_OPTIONS} \
  ${FORMAT_PANDOC_OPTIONS} \
  ${TOC_PANDOC_OPTIONS} \
  ${HTML_ASSETS_OPTIONS} \
-o ${BUILD_DIR}/html/kotlin-spec.html
