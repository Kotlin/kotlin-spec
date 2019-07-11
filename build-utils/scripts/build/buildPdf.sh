source directories.sh

export PROJECT_DIR

cd ${PROJECT_DIR}/docs

gpp -H ./index.md | pandoc \
--filter ../${HELPERS_DIRECTORY}/processTodoFilter.sh \
--filter ../${HELPERS_DIRECTORY}/markSentencesFilter.sh \
--filter ../${HELPERS_DIRECTORY}/copyPasteFilter.sh \
--filter ../${HELPERS_DIRECTORY}/inlineDiagramFilter.sh \
--filter ../${HELPERS_DIRECTORY}/inlineCodeIndentFilter.sh \
--filter ../${HELPERS_DIRECTORY}/mathCleanUpFilter.sh \
--toc --toc-depth=6 -H ./preamble.tex --variable documentclass=book \
-s -f markdown-raw_html+smart+tex_math_double_backslash \
-o ../${BUILD_DIRECTORY}/pdf/kotlin-spec.pdf
