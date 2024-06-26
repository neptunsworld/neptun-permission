import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(libs.bundles.database)
    implementation(libs.streamline.common)

    compileOnly(project(":permission-api"))
    implementation(project(":permission-common"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

task("sourcesJar", type = Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set("${project.name}-${project.version}.jar")
        mergeServiceFiles()

        manifest {
            attributes["Main-Class"] = "world.neptuns.permission.watchdog.WatchdogMain"
            attributes["Implementation-Version"] = project.version
        }

        dependsOn(":permission-common:shadowJar")
    }

    named<KotlinCompile>("compileKotlin") {
        dependsOn(":permission-common:shadowJar")
    }
}

tasks.withType<KaptWithoutKotlincTask> {
    dependsOn(":permission-common:shadowJar")
}