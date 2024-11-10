#!/usr/bin/env sh

set -eu

export BUILDKIT_PROGRESS=plain
export PROGRESS_NO_TRUNC=1

rm -rf build/failed

docker build . \
  --target build-output \
  --output build

if [ -f build/failed ]; then
  exit "$(cat build/failed)";
else
  docker build . "$@"
fi
