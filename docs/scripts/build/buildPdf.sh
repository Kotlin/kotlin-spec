source directories.sh
source ${BUILD_DIR}/prepare.sh
source settings.sh

init_settings "pdf"

export PROJECT_DIR
export TODO_OPTION
export STATIC_MATH_OPTION

IMAGE_DIR_OPTION="--image-directory=${BUILD_DIR}/pdf/images"
export IMAGE_DIR_OPTION

cd ${PROJECT_DIR}/src/md

gpp -H ./index.md | pandoc \
--filter ${FILTERS_DIR}/compoundFilter.sh \
  ${PREAMBLE_OPTIONS} \
  ${COMMON_PANDOC_OPTIONS} \
  ${FORMAT_PANDOC_OPTIONS} \
  ${TOC_PANDOC_OPTIONS} \
--variable documentclass=book \
--number-sections \
-o ${BUILD_DIR}/spec/pdf/kotlin-spec.pdf
