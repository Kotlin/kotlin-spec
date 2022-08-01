#!/bin/bash

cd /github/workspace

./gradlew buildWeb buildPdf

./gradlew :grammar:jar
