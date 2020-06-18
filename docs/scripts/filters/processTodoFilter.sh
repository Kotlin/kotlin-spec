export PROJECT_DIR
export TODO_OPTION

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.ProcessTodoFilterKt" -Pargs="$* $TODO_OPTION" execute -q
