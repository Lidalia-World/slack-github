plugins {
  id("buildlogic.kotlin-common-conventions")
  application
}

dependencies {
}

application {
  mainModule = "slackgithub.app"
  mainClass = "slackgithub.app.AppKt"
}
