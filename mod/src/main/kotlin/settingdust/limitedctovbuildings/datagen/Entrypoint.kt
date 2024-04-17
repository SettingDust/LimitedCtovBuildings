package settingdust.limitedctovbuildings.datagen

import com.google.gson.JsonObject
import dev.worldgen.lithostitched.registry.LithostitchedRegistries
import kotlin.streams.asSequence
import net.enderturret.patchedmod.data.PatchProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataOutput
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper

lateinit var registries: RegistryWrapper.WrapperLookup

class Entrypoint : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()
        pack.addProvider { _ -> CtovUseLithostitchedJigsawProvider(generator) }
    }
}

class CtovUseLithostitchedJigsawProvider(generator: DataGenerator) :
    PatchProvider(generator, DataOutput.OutputType.DATA_PACK, null) {

    override fun registerPatches() {
        registerAlternativeJigsaw()
        registerGuaranteedInjection()
    }

    private fun registerAlternativeJigsaw() {
        val pathToHandle = setOf("small", "medium", "large")
        val registry = registries.getWrapperOrThrow(RegistryKeys.STRUCTURE)
        for (reference in
            registry.streamEntries().asSequence().filter { entry ->
                val path = entry.registryKey().value.path
                pathToHandle.any { path.startsWith(it) }
            }) {
            patch(reference.registryKey().value.withPrefixedPath("worldgen/structure/"))
                .compound()
                .apply {
                    test("/type", "lithostitched:jigsaw", true)
                    replace("/type", "lithostitched:jigsaw")
                }
                .end()
        }
    }

    private fun registerGuaranteedInjection() {
        val registry = registries.getWrapperOrThrow(LithostitchedRegistries.WORLDGEN_MODIFIER)
        for (reference in registry.streamEntries().asSequence()) {
            patch(
                    reference
                        .registryKey()
                        .value
                        .withPrefixedPath("lithostitched/worldgen_modifier/")
                )
                .compound()
                .apply {
                    test("/elements/0/element/element_type", "lithostitched:guaranteed", true)
                    move("/delegate", "/elements/0/element")
                    add("/elements/0/element", JsonObject())
                    add("/elements/0/element/element_type", "lithostitched:guaranteed")
                    add("/elements/0/element/count", 1)
                    add("/elements/0/element/min_depth", 3)
                    move("/elements/0/element/delegate", "/delegate")
                }
                .end()
        }
    }
}
