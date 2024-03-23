package settingdust.limitedctovbuildings.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class DevMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(final List<String> targetClassNames, final String mixinClassName) {
        if (mixinClassName.equals("net.fabricmc.fabric.mixin.datagen.server.MainMixin"))
            return true;
        return false;
    }
}
