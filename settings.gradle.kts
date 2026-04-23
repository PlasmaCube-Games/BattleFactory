pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
        maven {
            name = "Quiet Loom"
            url = uri("https://repo.jpenilla.xyz/snapshots/")
        }
    }
}

rootProject.name = "CobblemonBattleFactory"