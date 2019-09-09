export PROJECT_DIR

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.InlineDiagramFilterKt" \
    -Pargs="$1 --embed" execute -q
