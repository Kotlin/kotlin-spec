export PROJECT_DIR

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.InlineDiagramFilterKt" \
    -Pargs="$* --embed" execute -q
