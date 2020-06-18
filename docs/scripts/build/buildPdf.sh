source directories.sh
source settings.sh

init_settings "pdf"

export PROJECT_DIR
export DISABLE_TODOS

cd ${PROJECT_DIR}/src/md

gpp -H ./index.md | pandoc \
--filter ${FILTERS_DIR}/processTodoFilter.sh \
--filter ${FILTERS_DIR}/markSentencesFilter.sh \
--filter ${FILTERS_DIR}/copyPasteFilter.sh \
--filter ${FILTERS_DIR}/inlineDiagramFilter.sh \
--filter ${FILTERS_DIR}/mathInCodeFilter.sh \
--filter ${FILTERS_DIR}/brokenReferencesReportFilter.sh \
  ${PREAMBLE_OPTIONS} \
  ${COMMON_PANDOC_OPTIONS} \
  ${FORMAT_PANDOC_OPTIONS} \
  ${TOC_PANDOC_OPTIONS} \
--variable documentclass=book \
--number-sections \
-o ${BUILD_DIR}/pdf/kotlin-spec.pdf
