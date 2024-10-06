plugins {
  id("buildlogic.kotlin-common-conventions")
  application
}

dependencies {
  implementation("org.apache.commons:commons-text")
  implementation(projects.utilities)
}

application {
  // Define the main class for the application.
  mainClass = "org.example.app.AppKt"
}
