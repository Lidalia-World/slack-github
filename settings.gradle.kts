pluginManagement {
  // Include 'plugins build' to define convention plugins.
  includeBuild("build-logic")

  repositories {
    gradlePluginPortal()
    maven {
      url = uri("https://public.mavenrepo.lidalia.org.uk/releases")
    }
  }
}

plugins {
  // Apply the foojay-resolver plugin to allow automatic download of JDKs
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "slack-github"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
  "app",
  "list",
  "utilities",
)
