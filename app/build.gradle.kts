plugins {
  id("buildlogic.kotlin-common-conventions")
  application
}

dependencies {
//  implementation(libs.aws.lambda.core)
//  implementation(libs.aws.lambda.events)
  implementation(platform(libs.http4k.bom))
  implementation(libs.http4k.core)
  implementation(libs.http4k.serverless.core)
  implementation(libs.http4k.serverless.lambda)
  implementation(libs.http4k.serverless.lambda.runtime)
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
