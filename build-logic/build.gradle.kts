plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.kotlinter.gradle.plugin)
}
