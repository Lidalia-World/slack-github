# syntax=docker/dockerfile:1.10.0
ARG username=worker
ARG gid=1000
ARG uid=1001
ARG work_dir=/home/$username/slackgithub
ARG base_modules=java.base,java.logging,java.management,java.net.http,jdk.httpserver
ARG jre_dir=/opt/jre

# Copy across all the build definition files in a separate stage
# This will not get any layer caching if anything in the context has changed, but when we
# subsequently copy them into a different stage that stage *will* get layer caching. So if none of
# the build definition files have changed, a subsequent command will also get layer caching.
FROM --platform=$BUILDPLATFORM busybox:1.37.0-musl AS gradle-files
RUN --mount=type=bind,target=/docker-context \
    mkdir -p /gradle-files/gradle && \
    cd /docker-context/ && \
    cp -R gradle /gradle-files/ && \
    cp gradlew /gradle-files/ && \
    find . -name "*.gradle" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "*.gradle.kts" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "libs.versions.toml" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name ".editorconfig" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "gradle.properties" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "*module-info.java" -exec cp --parents "{}" /gradle-files/ \; && \
    find . -name "gradle.lockfile" -exec cp --parents "{}" /gradle-files/ \;


FROM --platform=$BUILDPLATFORM eclipse-temurin:23.0.2_7-jdk-alpine AS base_builder

ARG username
ARG gid
ARG uid

RUN addgroup --system $username --gid $gid && \
    adduser --system $username --ingroup $username --uid $uid

USER $username

# Download gradle in a separate step to benefit from layer caching
COPY --chown=$uid gradle/wrapper gradle/wrapper
COPY --chown=$uid gradlew gradlew
RUN  ./gradlew --version

ARG work_dir
RUN mkdir -p $work_dir
WORKDIR $work_dir

ARG gradle_cache_dir=/home/$username/.gradle/caches

RUN mkdir -p $gradle_cache_dir

ENV GRADLE_OPTS="\
-Dorg.gradle.daemon=false \
-Dorg.gradle.logging.stacktrace=all \
-Dorg.gradle.logging.level=info \
-Dorg.gradle.vfs.watch=false \
-Dorg.gradle.console=plain \
"

ENV GRADLE_PROPS="\
-Pkotlin.compiler.execution.strategy=in-process \
-Pkotlin.compiler.runViaBuildToolsApi=true \
"

# Build the configuration cache & download all deps in a single layer
COPY --chown=$uid --from=gradle-files /gradle-files ./
RUN  --mount=type=cache,gid=$gid,uid=$uid,target=$work_dir/.gradle \
     --mount=type=cache,gid=$gid,uid=$uid,target=$gradle_cache_dir \
     ./gradlew $GRADLE_PROPS build --dry-run

COPY --chown=$uid . .

# So the tests can run without network access. Proves no tests rely on external services.
RUN --mount=type=cache,gid=$gid,uid=$uid,target=$work_dir/.gradle \
    --mount=type=cache,gid=$gid,uid=$uid,target=$gradle_cache_dir \
    --network=none \
    ./gradlew $GRADLE_PROPS --offline build || (status=$?; mkdir -p build && echo $status > build/failed)


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
    if [ -f build/failed ]; then ./gradlew $GRADLE_PROPS --offline build; fi

ARG base_modules
RUN ./checkModules.sh "$work_dir/build/project/artifacts/lib" "$base_modules"


FROM eclipse-temurin:23.0.2_7-jdk-alpine AS small_jre_builder

ARG base_modules
ARG jre_dir

COPY --link prepareSmallJre.sh .
RUN ./prepareSmallJre.sh "$base_modules" $jre_dir


FROM busybox:1.37.0-musl AS slackgithub

ARG username
ARG work_dir
ARG gid
ARG uid

RUN addgroup --system $username --gid $gid && \
    adduser --system $username --ingroup $username --uid $uid

USER $username
RUN mkdir -p $work_dir
WORKDIR $work_dir

ARG jre_dir
ENV JAVA_HOME=$jre_dir
ENV PATH="$jre_dir/bin:$PATH"

COPY --link --from=small_jre_builder --chown=root:root $jre_dir $jre_dir
COPY --link --from=alpine:3.20.3 --chown=root:root /lib/ld-musl-* /lib/
COPY --link --from=builder --chown=root:root $work_dir/build/project/artifacts/lib/ /opt/slack-github/

ENTRYPOINT [ "java", "-XX:+UseZGC", "-jar", "/opt/slack-github/app.jar" ]
