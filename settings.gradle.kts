pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "neptun-permission"
include("permission-api")
include("permission-common")
include("permission-bukkit")
include("permission-velocity")
include("permission-watchdog")
