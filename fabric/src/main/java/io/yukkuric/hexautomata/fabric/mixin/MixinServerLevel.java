package io.yukkuric.hexautomata.fabric.mixin;

import io.yukkuric.hexautomata.events.CommonEventsHandler;
import io.yukkuric.hexautomata.events.EventMarker;
import io.yukkuric.hexautomata.fabric.events.HAEventsFabric;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class MixinServerLevel {
    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    void hookAddEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if ((entity instanceof Projectile proj && proj.getOwner() instanceof ServerPlayer player)) {
            CommonEventsHandler.get(EventMarker.SHOOT.INSTANCE).invoke(player, new HAEventsFabric.
                    Shoot(Projectile.class.cast(this)));
        }
    }
}
