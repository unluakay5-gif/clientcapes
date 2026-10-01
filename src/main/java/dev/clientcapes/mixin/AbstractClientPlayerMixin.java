package dev.clientcapes.mixin;

import dev.clientcapes.CapeConfig;
import dev.clientcapes.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void clientcapes$overrideCape(CallbackInfoReturnable<PlayerSkin> cir) {
        CapeConfig cfg = CapeConfig.INSTANCE;
        if (!cfg.enabled) return;
        // Only ever touch the local player - nobody else sees this
        if ((Object) this != Minecraft.getInstance().player) return;

        ClientAsset.Texture cape = CapeManager.get(cfg.selectedCape);
        if (cape == null) return;

        PlayerSkin o = cir.getReturnValue();
        cir.setReturnValue(new PlayerSkin(o.body(), cape, cape, o.model(), o.secure()));
    }
}
