
KOTLIN_COMPILER_DIR=`mktemp -d`
PROJECT_DIR=`pwd`

cd ${KOTLIN_COMPILER_DIR}

git init

git sparse-checkout init
git sparse-checkout set "compiler/testData/psi" \
                        "compiler/testData/diagnostics/tests"

git remote add origin https://github.com/JetBrains/kotlin
git fetch origin master
git pull origin master

cp -R compiler/testData/psi ${PROJECT_DIR}/testData/psi
cp -R compiler/testData/diagnostics/tests ${PROJECT_DIR}/testData/diagnostics

rm -rf ${KOTLIN_COMPILER_DIR}
