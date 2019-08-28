source directories.sh
source settings.sh

init_settings "pdf"

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

mkdir -p ${BUILD_DIR}/pdf/sections

for f in $TMP_DIR/*.json;
do \
  pandoc $f --variable documentclass=book -o ${BUILD_DIR}/pdf/sections/"$(basename "$f" .json).pdf";
done

rm -rf $TMP_DIR
