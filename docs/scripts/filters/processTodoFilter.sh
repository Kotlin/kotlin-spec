export PROJECT_DIR
export DISABLE_TODOS

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.ProcessTodoFilterKt" -Pargs="$* $DISABLE_TODOS" execute -q
