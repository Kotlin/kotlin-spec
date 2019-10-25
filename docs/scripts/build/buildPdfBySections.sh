source directories.sh
source settings.sh

init_settings "pdf"

export PROJECT_DIR

cd ${PROJECT_DIR}/src/md

TMP_DIR=${BUILD_DIR}/~tmp

mkdir -p $TMP_DIR

gpp -H ./index.md \
| pandoc ${PREAMBLE_OPTIONS} ${COMMON_PANDOC_OPTIONS} -t json \
| bash ${FILTERS_DIR}/processTodoFilter.sh latex \
| bash ${FILTERS_DIR}/markSentencesFilter.sh latex \
| bash ${FILTERS_DIR}/copyPasteFilter.sh latex \
| bash ${FILTERS_DIR}/inlineDiagramFilter.sh latex \
| bash ${FILTERS_DIR}/inlineCodeIndenterFilter.sh latex \
| bash ${FILTERS_DIR}/brokenReferencesReportFilter.sh latex \
| bash ${FILTERS_DIR}/splitSections.sh "--output-directory=$TMP_DIR --format=latex"

mkdir -p ${BUILD_DIR}/pdf/sections

for f in $TMP_DIR/*.json;
do \
  pandoc ${PREAMBLE_OPTIONS} $f --variable documentclass=book -o ${BUILD_DIR}/pdf/sections/"$(basename "$f" .json).pdf";
done

rm -rf $TMP_DIR
