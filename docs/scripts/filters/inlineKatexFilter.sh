export PROJECT_DIR

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.InlineKatexKt" -Pargs=$1 execute -q
