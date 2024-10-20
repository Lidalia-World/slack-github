#!/usr/bin/env sh
set -eu

main() {
  class_path=$1
  expected_deps=$2

  java_deps=$(find_java_deps "$class_path")

  if [ "$java_deps" != "$expected_deps" ]; then
    >&2 echo "Project depends on $java_deps not $expected_deps"
    >&2 echo "Update jlink to bring in exactly:"
    >&2 echo "$java_deps"
    exit 1
  fi
}

find_java_deps() {
  class_path=$1
  # we want to split class_path
  # shellcheck disable=SC2086
  jdeps \
      --multi-release 17 \
      --class-path "$class_path" \
      -R \
      -s \
      $class_path/* \
    | grep 'java\.' \
    | cut -d' ' -f3 \
    | sort -u \
    | paste -sd "," -
}

main "$@"
