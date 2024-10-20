@file:Suppress("UnstableApiUsage")

import org.gradle.api.JavaVersion.VERSION_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("org.jmailen.kotlinter")
  id("uk.org.lidalia.ideaext")
}

repositories {
  mavenCentral()
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter("5.10.3")
    }
  }
}

java {
  sourceCompatibility = VERSION_21
  targetCompatibility = VERSION_21
}

tasks.withType<KotlinCompile> {
  compilerOptions.jvmTarget = JVM_21
}
