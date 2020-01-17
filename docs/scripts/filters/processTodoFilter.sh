#!/bin/bash
export PROJECT_DIR

cd $PROJECT_DIR/../ && ./gradlew -PmainClass="org.jetbrains.kotlin.spec.ProcessTodoFilterKt" -Pargs=$1 execute -q
