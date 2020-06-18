export PROJECT_DIR

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.CopyPasteFilterKt" -Pargs="$*" execute -q
