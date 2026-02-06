#!/bin/sh

DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_EXE="java"

exec "$JAVA_EXE" -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
