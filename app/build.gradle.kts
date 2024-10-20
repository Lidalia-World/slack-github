plugins {
  id("buildlogic.kotlin-common-conventions")
  application
}

dependencies {
  implementation(projects.awsLambdaRuntime)
  implementation(libs.http4k.core)
}

application {
  mainClass = "slackgithub.app.AppKt"
}

tasks {
  assemble {
    dependsOn(installDist)
  }
}

tasks.jar {
  manifest {
    attributes["Main-Class"] = application.mainClass
    attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
  }
}
