@file:Suppress("UnstableApiUsage")

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("org.jmailen.kotlinter")
  id("uk.org.lidalia.ideaext")
}

private val illegalModuleCharacter: Regex = "[^a-z0-9.]".toRegex()

fun String.normalise(): String = replace(illegalModuleCharacter, "")

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

val moduleName = "${project.group}.${project.name}".normalise()

// this is needed because 'module-info.java' is in 'main/java' and the Kotlin code is in 'main/kotlin'
tasks.compileJava {
  // Compiling module-info in the 'main/java' folder needs to see already compiled Kotlin code
  options.compilerArgs = listOf("--patch-module", "$moduleName=${sourceSets.main.get().output.asPath}")
}

