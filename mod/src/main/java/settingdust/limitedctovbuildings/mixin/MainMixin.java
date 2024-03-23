package settingdust.limitedctovbuildings.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import settingdust.limitedctovbuildings.datagen.EntrypointKt;

@Mixin(Main.class)
public class MainMixin {
    @Inject(
        method = "main",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.BY,
            by = 2,
            target = "Lnet/minecraft/registry/CombinedDynamicRegistries;getCombinedRegistryManager()" +
                     "Lnet/minecraft/registry/DynamicRegistryManager$Immutable;"
        ),
        cancellable = true
    )
    private static void callDataGen(
        String[] args,
        CallbackInfo info,
        @Local DynamicRegistryManager.Immutable registryManager
    ) {
        if (FabricDataGenHelper.ENABLED) {
            EntrypointKt.setRegistries(registryManager);
            FabricDataGenHelper.run();
            info.cancel();
        }
    }
}
