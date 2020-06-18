export PROJECT_DIR
export STATIC_MATH_OPTION

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.InlineKatexKt" -Pargs="$* $STATIC_MATH_OPTION" execute -q
