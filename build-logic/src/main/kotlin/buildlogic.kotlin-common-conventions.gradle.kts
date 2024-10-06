@file:Suppress("UnstableApiUsage")

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
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}
