source directories.sh
source settings.sh

init_settings "html"

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
${HTML_ASSETS_OPTIONS} \
-o ../${BUILD_DIRECTORY}/html/kotlin-spec.html
