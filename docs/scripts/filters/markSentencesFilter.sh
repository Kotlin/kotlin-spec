export PROJECT_DIR

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.MarkSentencesFilterKt" -Pargs="$*" execute -q