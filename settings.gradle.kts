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
  id("com.autonomousapps.build-health") version "2.8.2"
  id("org.jetbrains.kotlin.jvm") version "2.1.10" apply false
}

rootProject.name = "slack-github"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
  "app",
)
