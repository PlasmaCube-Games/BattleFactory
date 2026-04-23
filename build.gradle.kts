@file:Suppress("UnstableApiUsage")

plugins {
    java
    idea
    id("quiet-fabric-loom") version ("1.9-SNAPSHOT")
    id("org.jetbrains.kotlin.jvm").version("2.2.0")
}

val modId = project.properties["mod_id"].toString()
version = project.properties["mod_version"].toString()
group = project.properties["mod_group"].toString()

val modName = project.properties["mod_name"].toString()
base.archivesName.set(modName)

val minecraftVersion = project.properties["minecraft_version"].toString()

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.parchmentmc.org")
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots1"
        mavenContent { snapshotsOnly() }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

loom {
    mixin {
        defaultRefmapName.set("cobblemonbattlefactory.refmap.json")
    }
    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21:${project.properties["parchment_version"]}")
    })

    modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.properties["fabric_kotlin_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")

    modImplementation("maven.modrinth:cobblemon:${project.properties["cobblemon_version"]}")

    modImplementation(include("eu.pb4:sgui:${project.properties["sgui_version"]}")!!)
    modImplementation(include("net.kyori:adventure-platform-fabric:${project.properties["adventure_platform_version"]}")!!)
    modImplementation(include("me.lucko:fabric-permissions-api:${project.properties["fabric_permissions_api_version"]}")!!)

    compileOnly("net.luckperms:api:5.4")
}

tasks.processResources {
    inputs.property("version", version)
    inputs.property("id", modId)

    filesMatching("fabric.mod.json") {
        expand("id" to modId, "version" to version)
    }
}

tasks.remapJar {
    archiveFileName.set("${modName}-${project.version}.jar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}
