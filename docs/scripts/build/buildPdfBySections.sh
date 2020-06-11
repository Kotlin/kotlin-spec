source directories.sh
source settings.sh

init_settings "pdf"

export PROJECT_DIR

cd ${PROJECT_DIR}/src/md

TMP_DIR=${BUILD_DIR}/~tmp

mkdir -p $TMP_DIR

touch $TMP_DIR/p0
touch $TMP_DIR/p1

gpp -H ./index.md \
| pandoc ${PREAMBLE_OPTIONS} ${COMMON_PANDOC_OPTIONS} ${FORMAT_PANDOC_OPTIONS} -t json >$TMP_DIR/p1 \
&& bash ${FILTERS_DIR}/processTodoFilter.sh latex <$TMP_DIR/p1 >$TMP_DIR/p0 \
&& bash ${FILTERS_DIR}/markSentencesFilter.sh latex <$TMP_DIR/p0 >$TMP_DIR/p1 \
&& bash ${FILTERS_DIR}/copyPasteFilter.sh latex <$TMP_DIR/p1 >$TMP_DIR/p0 \
&& bash ${FILTERS_DIR}/inlineDiagramFilter.sh latex <$TMP_DIR/p0 >$TMP_DIR/p1 \
&& bash ${FILTERS_DIR}/mathInCodeFilter.sh latex <$TMP_DIR/p1 >$TMP_DIR/p0 \
&& bash ${FILTERS_DIR}/brokenReferencesReportFilter.sh latex <$TMP_DIR/p0 >$TMP_DIR/p1 \
&& bash ${FILTERS_DIR}/splitSections.sh "--output-directory=$TMP_DIR --format=latex" <$TMP_DIR/p1

mkdir -p ${BUILD_DIR}/pdf/sections

for f in $TMP_DIR/*.json;
do \
  pandoc \
    ${PREAMBLE_OPTIONS} \
    ${COMMON_PANDOC_OPTIONS} \
    $f \
    --variable documentclass=book \
    --number-sections \
    -o ${BUILD_DIR}/pdf/sections/"$(basename "$f" .json).pdf";
done

rm -rf $TMP_DIR
