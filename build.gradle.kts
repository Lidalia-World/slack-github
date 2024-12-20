import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gundy.semver4j.model.Version
import org.gradle.api.distribution.plugins.DistributionPlugin.TASK_INSTALL_NAME
import java.nio.file.Path

plugins {
  base
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlinter)
  alias(libs.plugins.taskTree)
  alias(libs.plugins.versions)
}

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath(group = "com.github.gundy", name = "semver4j", version = "0.16.4")
  }
}

tasks {
  register<Copy>("copyDistribution") {
    val app = project(":app")
    dependsOn(app.tasks.getByName("installDist"))
    from(app.tasks.getByName(TASK_INSTALL_NAME))
    into(project.layout.buildDirectory.dir("artifacts"))
  }

  assemble {
    dependsOn("copyDistribution")
  }

  check {
    dependsOn("buildHealth")
    dependsOn("installKotlinterPrePushHook")
  }
}

dependencyAnalysis {
  issues {
    // configure for all projects
    all {
      // set behavior for all issue types
      onAny {
        severity("fail")
      }
      onUnusedDependencies {
        exclude(libs.junit.jupiter.asProvider())
      }
    }
  }
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    candidate.version.isPreRelease()
  }
}

val initialBuildDir = rootProject.layout.buildDirectory.get()
rootProject.layout.buildDirectory = initialBuildDir.dir("project")

subprojects {
  val relativeProjectPath: Path = rootProject.projectDir.toPath().relativize(projectDir.toPath())
  layout.buildDirectory = rootProject.layout.buildDirectory.get()
    .dir("child-projects")
    .dir(relativeProjectPath.toString())
}

fun String.isPreRelease(): Boolean = try {
  Version.fromString(this).preReleaseIdentifiers.isNotEmpty()
} catch (e: IllegalArgumentException) {
  false
}
