export PROJECT_DIR

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.BrokenReferencesReporterKt" -Pargs="$*" execute -q
