package io.yukkuric.hexautomata.mixin;

import io.yukkuric.hexautomata.events.CommonHelpers;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MixinMob extends Entity {
    private MixinMob() {
        super(null, null);
    }

    @Shadow
    public abstract LivingEntity getTarget();

    @Inject(method = "serverAiStep", at = @At("RETURN"))
    void updateTargeting(CallbackInfo ci) {
        CommonHelpers.compareAndTriggerTargeted(this, getTarget());
    }
}
