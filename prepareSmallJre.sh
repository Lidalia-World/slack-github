#!/usr/bin/env sh
set -eu

main() {
  base_modules=$1
  build_jvm_dir=$2

  jlink \
      --add-modules "$base_modules" \
      --strip-debug \
      --no-header-files \
      --no-man-pages \
      --output "$build_jvm_dir"

  strip -p --strip-unneeded $(find "$build_jvm_dir" -name *.so)
}

main "$@"
