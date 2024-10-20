# syntax=docker/dockerfile:1.10.0
ARG username=worker
ARG gid=1000
ARG uid=1001
ARG work_dir=/home/$username/work
ARG base_modules=java.base
ARG jre_dir=/opt/jre

# Copy across all the build definition files in a separate stage
# This will not get any layer caching if anything in the context has changed, but when we
# subsequently copy them into a different stage that stage *will* get layer caching. So if none of
# the build definition files have changed, a subsequent command will also get layer caching.
FROM --platform=$BUILDPLATFORM busybox:1.37.0-musl AS gradle-files
RUN --mount=type=bind,target=/docker-context \
    mkdir -p /gradle-files/gradle && \
    cd /docker-context/ && \
    find . -name "*.gradle" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "*.gradle.kts" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "libs.versions.toml" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name ".editorconfig" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "gradle.properties" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "*module-info.java" -exec cp --parents "{}" /gradle-files/ \;


FROM --platform=$BUILDPLATFORM eclipse-temurin:23_37-jdk-alpine AS base_builder

ARG username
ARG work_dir
ARG gid
ARG uid

RUN addgroup --system $username --gid $gid && \
    adduser --system $username --ingroup $username --uid $uid

USER $username
RUN mkdir -p $work_dir
WORKDIR $work_dir

ARG gradle_cache_dir=/home/$username/.gradle

RUN mkdir -p $gradle_cache_dir

ENV GRADLE_OPTS="\
-Dorg.gradle.daemon=false \
-Dorg.gradle.logging.stacktrace=all \
-Dorg.gradle.vfs.watch=false \
-Dorg.gradle.console=plain \
"

# Download gradle in a separate step to benefit from layer caching
COPY --link --chown=$uid gradle/wrapper gradle/wrapper
COPY --link --chown=$uid gradlew gradlew
RUN  --mount=type=cache,gid=$gid,uid=$uid,target=$work_dir/.gradle \
     --mount=type=cache,gid=$gid,uid=$uid,target=$gradle_cache_dir \
     ./gradlew --version

# Build the configuration cache & download all deps in a single layer
COPY --link --chown=$uid --from=gradle-files /gradle-files ./
RUN  --mount=type=cache,gid=$gid,uid=$uid,target=$work_dir/.gradle \
     --mount=type=cache,gid=$gid,uid=$uid,target=$gradle_cache_dir \
     ./gradlew build --dry-run

COPY --link --chown=$uid . .

# So the tests can run without network access. Proves no tests rely on external services.
RUN --mount=type=cache,gid=$gid,uid=$uid,target=$work_dir/.gradle \
    --mount=type=cache,gid=$gid,uid=$uid,target=$gradle_cache_dir \
    --network=none \
    ./gradlew --offline build || (status=$?; mkdir -p build && echo $status > build/failed)


FROM --platform=$BUILDPLATFORM scratch AS build-output
ARG work_dir

COPY --link --from=base_builder $work_dir/build .

# The base_builder step is guaranteed not to fail, so that the build output can be extracted.
# You run this as:
# `docker build . --target build-output --output build && docker build .`
# to retrieve the build reports whether or not the previous line exited successfully.
# Workaround for https://github.com/moby/buildkit/issues/1421
FROM --platform=$BUILDPLATFORM base_builder AS builder
RUN --mount=type=cache,gid=$gid,uid=$uid,target=$work_dir/.gradle \
    --mount=type=cache,gid=$gid,uid=$uid,target=$gradle_cache_dir \
    if [ -f build/failed ]; then ./gradlew --offline build; fi

RUN tar -xf build/child-projects/app/distributions/app.tar -C build/child-projects/app/distributions


FROM eclipse-temurin:23_37-jdk-alpine AS small_jre_builder

ARG base_modules
ARG jre_dir

COPY --link prepareSmallJre.sh .
RUN ./prepareSmallJre.sh "$base_modules" $jre_dir


FROM busybox:1.37.0-musl

ARG jre_dir
ARG username
ARG work_dir
ARG gid
ARG uid

RUN addgroup --system $username --gid $gid && \
    adduser --system $username --ingroup $username --uid $uid

COPY --link --from=small_jre_builder $jre_dir $jre_dir
COPY --link --from=alpine:3.20.3 /lib/ld-musl-* /lib/
ENV JAVA_HOME=$jre_dir
ENV PATH="$jre_dir/bin:$PATH"

USER $username
RUN mkdir -p $work_dir
WORKDIR $work_dir

COPY --link --from=builder --chown=root:root $work_dir/build/child-projects/app/distributions/app/lib/ /opt/slack-github/

ENTRYPOINT [ "java", "--module-path", "/opt/slack-github", "-m", "slackgithub.app" ]
