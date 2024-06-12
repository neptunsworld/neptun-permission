import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
}

allprojects {
    group = "world.neptuns.permission"
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            url = uri("https://nexus.neptuns.world/repository/maven-public/")
            credentials {
                username = property("nexusUsername") as String
                password = property("nexusPassword") as String
            }
        }
        mavenCentral()
    }

    apply(plugin = "java")
    apply(plugin = "maven-publish")

    afterEvaluate {
        dependencies {
            compileOnly(libs.gson)
            compileOnly(libs.guava)
            compileOnly(libs.bundles.database)
            compileOnly(libs.bundles.streamline)
            compileOnly(libs.bundles.kyori)
            compileOnly(libs.neptun.core.base.api)
        }
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = libs.versions.java.get()
}