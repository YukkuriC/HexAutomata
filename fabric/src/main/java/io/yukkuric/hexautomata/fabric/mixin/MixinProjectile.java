package io.yukkuric.hexautomata.fabric.mixin;

import io.yukkuric.hexautomata.events.*;
import io.yukkuric.hexautomata.fabric.events.HAEventsFabric;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class MixinProjectile extends Entity {
    MixinProjectile() {
        super(null, null);
    }

    @Shadow
    @Nullable
    public abstract Entity getOwner();

    @Inject(method = "onHit", at = @At("HEAD"))
    void hookOnHit(HitResult hitResult, CallbackInfo ci) {
        if (!(getOwner() instanceof ServerPlayer player)) return;
        var event = new HAEventsFabric.ProjectileHit(Projectile.class.cast(this), hitResult);
        if (event.getInvalid()) return;
        CommonEventsHandler.get(BuiltinEventMarker.PROJECTILE_HIT.INSTANCE).invoke(player, event);
    }
}
