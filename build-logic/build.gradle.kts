plugins {
  `kotlin-dsl`
  alias(libs.plugins.kotlinter) apply true
}

repositories {
  gradlePluginPortal()
  maven {
    url = uri("https://public.mavenrepo.lidalia.org.uk/releases")
  }
}

dependencies {
  // https://github.com/gradle/gradle/issues/15383
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.kotlinter.gradle.plugin)
  implementation(libs.ideaext.gradle.plugin)
}
