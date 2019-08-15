source directories.sh
source settings.sh

init_settings "pdf"

export PROJECT_DIR

cd ${PROJECT_DIR}/docs

gpp -H ./index.md | pandoc \
--filter ../${HELPERS_DIRECTORY}/processTodoFilter.sh \
--filter ../${HELPERS_DIRECTORY}/markSentencesFilter.sh \
--filter ../${HELPERS_DIRECTORY}/copyPasteFilter.sh \
--filter ../${HELPERS_DIRECTORY}/inlineDiagramFilter.sh \
--filter ../${HELPERS_DIRECTORY}/inlineCodeIndentFilter.sh \
--filter ../${HELPERS_DIRECTORY}/mathCleanUpFilter.sh \
${PREAMBLE_OPTIONS} \
${COMMON_PANDOC_OPTIONS} \
${TOC_PANDOC_OPTIONS} \
--variable documentclass=book \
-o ../${BUILD_DIRECTORY}/pdf/kotlin-spec.pdf
