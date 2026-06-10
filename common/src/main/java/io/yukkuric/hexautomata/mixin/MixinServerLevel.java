// 生成于 GLM-5V-Turbo
package io.yukkuric.hexautomata.mixin;

import io.yukkuric.hexautomata.events.BuiltinEventMarker;
import io.yukkuric.hexautomata.events.CommonEventsHandler;
import io.yukkuric.hexautomata.events.IHAEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    @Inject(method = "addFreshEntity", at = @At("RETURN"))
    private void onAddFreshEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Projectile proj && proj.getOwner() instanceof ServerPlayer owner) {
            CommonEventsHandler.trigger(BuiltinEventMarker.SHOOT.INSTANCE, owner, new IHAEvent.CommonProjShoot(proj));
        }
    }
}
