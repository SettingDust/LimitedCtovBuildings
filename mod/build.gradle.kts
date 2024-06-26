import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    alias(catalog.plugins.fabric.loom)

    alias(catalog.plugins.kotlin.jvm)
    alias(catalog.plugins.kotlin.plugin.serialization)
}

val id: String by rootProject.properties
val name: String by rootProject.properties
val author: String by rootProject.properties
val description: String by rootProject.properties

archivesName = name

repositories { maven("https://jitpack.io") { content { includeGroupAndSubgroups("com.github") } } }

fabricApi { configureDataGeneration() }

loom {
    accessWidenerPath = file("src/main/resources/$id.accesswidener")

    runs {
        configureEach { ideConfigGenerated(true) }
        named("client") { name("Fabric Client") }
        named("server") { name("Fabric Server") }
    }
}

dependencies {
    minecraft(catalog.minecraft)
    mappings(variantOf(catalog.yarn) { classifier("v2") })

    modImplementation(catalog.fabric.loader)
    modImplementation(catalog.fabric.api)
    modImplementation(catalog.fabric.kotlin)

    modRuntimeOnly(catalog.modmenu)

    modImplementation(catalog.patched)

    modImplementation(catalog.lithostitched)

    modImplementation(catalog.ctov)

    implementation(annotationProcessor(catalog.mixinsquared.get()) {})
}

kotlin { jvmToolchain(17) }

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

val metadata =
    mapOf(
        "group" to group,
        "author" to author,
        "id" to id,
        "name" to name,
        "version" to version,
        "description" to description,
        "source" to "https://github.com/SettingDust/LimitedCtovBuildings",
        "minecraft" to "~1.20",
        "fabric_loader" to ">=0.12",
        "fabric_kotlin" to ">=1.10",
        "modmenu" to "*",
    )

tasks {
    withType<ProcessResources> {
        inputs.properties(metadata)
        filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(metadata) }
    }

    jar {
        enabled = false
        from("LICENSE")
    }

    remapJar { enabled = false }

    ideaSyncTask { enabled = true }

    val buildDatapack by
        creating(Zip::class) {
            dependsOn("runDatagen")
            from(sourceSets.main.get().resources)
            include("pack.mcmeta", "pack.png", "data/**")
            destinationDirectory = project.layout.buildDirectory.dir("libs")
        }

    build { dependsOn(buildDatapack) }
}
