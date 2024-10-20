plugins {
  id("buildlogic.kotlin-common-conventions")
}

dependencies {
  api(platform(libs.http4k.bom))
  api(libs.http4k.core)
  implementation(libs.http4k.format.moshi)
}

dependencyAnalysis {
  issues {
    onUnusedDependencies {
      exclude(libs.junit.jupiter.api)
    }
  }
}
