#!/bin/bash
set -eu
./gradlew wrapper --gradle-version $1 --distribution-type all
