source directories.sh
source settings.sh

init_settings "pdf" "section"

export PROJECT_DIR

cd ${PROJECT_DIR}/docs

cat ./commands.md ./${SECTIONS_DIRECTORY}/<section>.md >> ./~temp.md

gpp -H ./~temp.md | pandoc \
--filter ../${HELPERS_DIRECTORY}/processTodoFilter.sh \
--filter ../${HELPERS_DIRECTORY}/markSentencesFilter.sh \
--filter ../${HELPERS_DIRECTORY}/copyPasteFilter.sh \
--filter ../${HELPERS_DIRECTORY}/inlineDiagramFilter.sh \
--filter ../${HELPERS_DIRECTORY}/inlineCodeIndentFilter.sh \
--filter ../${HELPERS_DIRECTORY}/mathCleanUpFilter.sh \
${PREAMBLE_OPTIONS} \
${COMMON_PANDOC_OPTIONS} \
--variable documentclass=book \
-o ../${BUILD_DIRECTORY}/pdf/sections/<section>.pdf

rm ./~temp.md

cd $OLDPWD
