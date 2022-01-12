#!/bin/env bash
java -cp $PROJECT_DIR/build/libs/filters.jar org.jetbrains.kotlin.spec.BrokenReferencesReporterKt $*
