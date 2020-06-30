source directories.sh
source settings.sh

init_settings "html"

export PROJECT_DIR
export TODO_OPTION
export STATIC_MATH_OPTION

cd ${PROJECT_DIR}/src/md

TMP_DIR=${BUILD_DIR}/~tmp

mkdir -p $TMP_DIR

touch $TMP_DIR/p0
touch $TMP_DIR/p1

gpp -H ./index.md \
| pandoc ${PREAMBLE_OPTIONS} ${COMMON_PANDOC_OPTIONS} ${FORMAT_PANDOC_OPTIONS} -t json >$TMP_DIR/p1 \
&& bash ${FILTERS_DIR}/processTodoFilter.sh html <$TMP_DIR/p1 >$TMP_DIR/p0 \
&& bash ${FILTERS_DIR}/markSentencesFilter.sh html <$TMP_DIR/p0 >$TMP_DIR/p1 \
&& bash ${FILTERS_DIR}/copyPasteFilter.sh html <$TMP_DIR/p1 >$TMP_DIR/p0 \
&& bash ${FILTERS_DIR}/inlineDiagramFilter.sh html <$TMP_DIR/p0 >$TMP_DIR/p1 \
&& bash ${FILTERS_DIR}/mathInCodeFilter.sh html <$TMP_DIR/p1 >$TMP_DIR/p0 \
&& bash ${FILTERS_DIR}/brokenReferencesReportFilter.sh html <$TMP_DIR/p0 >$TMP_DIR/p1 \
&& bash ${FILTERS_DIR}/inlineKatexFilter.sh html <$TMP_DIR/p1 >$TMP_DIR/p0 \
&& bash ${FILTERS_DIR}/splitSections.sh --output-directory=$TMP_DIR --generate-toc <$TMP_DIR/p0

mkdir -p ${BUILD_DIR}/html

for f in $TMP_DIR/*.json;
do \
  pandoc \
    ${PREAMBLE_OPTIONS} \
    ${COMMON_PANDOC_OPTIONS} \
    $f \
    ${HTML_ASSETS_OPTIONS} \
    -o ${BUILD_DIR}/html/"$(basename "$f" .json).html";
done

rm -rf $TMP_DIR
