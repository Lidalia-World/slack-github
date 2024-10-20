import org.gradle.api.JavaVersion.VERSION_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

layout.buildDirectory = rootProject.layout.projectDirectory
  .dir("..")
  .dir("build")
  .dir("build-logic")

java {
  sourceCompatibility = VERSION_21
  targetCompatibility = VERSION_21
}


tasks.withType<KotlinCompile> {
  compilerOptions.jvmTarget = JVM_21
}
