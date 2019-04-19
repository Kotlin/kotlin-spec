source directories.sh

export PROJECT_DIR

cd ${PROJECT_DIR}/docs

cat ./commands.md ./${SECTIONS_DIRECTORY}/<section>.md >> ./~temp.md

gpp -H ./~temp.md | pandoc \
--filter ../${HELPERS_DIRECTORY}/processTodoFilter.sh \
--filter ../${HELPERS_DIRECTORY}/markSentencesFilter.sh \
--filter ../${HELPERS_DIRECTORY}/copyPasteFilter.sh \
--filter ../${HELPERS_DIRECTORY}/inlineDiagrams.sh \
-H ./preamble.tex --variable documentclass=book \
-s -f markdown-raw_html+smart+tex_math_double_backslash \
-o ../${BUILD_DIRECTORY}/pdf/sections/<section>.pdf

rm ./~temp.md

cd $OLDPWD
