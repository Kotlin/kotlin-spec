source directories.sh
source settings.sh

init_settings "html" "section"

export PROJECT_DIR

cd ${PROJECT_DIR}/src/md

TMP_DIR=${BUILD_DIR}/~tmp

mkdir -p $TMP_DIR

gpp -H ./index.md \
| pandoc ${PREAMBLE_OPTIONS} ${COMMON_PANDOC_OPTIONS} -t json \
| bash ${FILTERS_DIR}/processTodoFilter.sh html \
| bash ${FILTERS_DIR}/markSentencesFilter.sh html \
| bash ${FILTERS_DIR}/copyPasteFilter.sh html \
| bash ${FILTERS_DIR}/inlineDiagramFilter.sh html \
| bash ${FILTERS_DIR}/inlineCodeIndenterFilter.sh html \
| bash ${FILTERS_DIR}/mathCleanUpFilter.sh html \
| bash ${FILTERS_DIR}/splitSections.sh --output-directory=$TMP_DIR

mkdir -p ${BUILD_DIR}/html/sections

for f in $TMP_DIR/*.json;
do \
  pandoc ${PREAMBLE_OPTIONS} $f ${HTML_ASSETS_OPTIONS} -s -o ${BUILD_DIR}/html/sections/"$(basename "$f" .json).html";
done

rm -rf $TMP_DIR
