source directories.sh

export PROJECT_DIR

cd ${PROJECT_DIR}/docs

TMP_DIR=$(pwd)/../${BUILD_DIRECTORY}/~tmp

mkdir -p $TMP_DIR

gpp -H ./index.md \
| pandoc -H ./preamble.md -s -f markdown-raw_html+smart+tex_math_double_backslash -t json \
| bash ../${HELPERS_DIRECTORY}/processTodoFilter.sh html \
| bash ../${HELPERS_DIRECTORY}/markSentencesFilter.sh html \
| bash ../${HELPERS_DIRECTORY}/copyPasteFilter.sh html \
| bash ../${HELPERS_DIRECTORY}/inlineDiagramFilter.sh html \
| bash ../${HELPERS_DIRECTORY}/inlineCodeIndentFilter.sh html \
| bash ../${HELPERS_DIRECTORY}/mathCleanUpFilter.sh html \
| bash ../${HELPERS_DIRECTORY}/splitSections.sh --output-directory=$TMP_DIR

mkdir -p ../${BUILD_DIRECTORY}/html/sections

for f in $TMP_DIR/*.json;
do \
pandoc $f -c ../${ASSETS_DIRECTORY}/css/main.css --katex=../${ASSETS_DIRECTORY}/js/katex/ -s -o ../${BUILD_DIRECTORY}/html/sections/"$(basename "$f" .json).html";
done

rm -rf $TMP_DIR
