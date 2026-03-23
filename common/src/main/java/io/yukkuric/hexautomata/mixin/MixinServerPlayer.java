package io.yukkuric.hexautomata.mixin;

import com.mojang.authlib.GameProfile;
import io.yukkuric.hexautomata.events.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player {
    public MixinServerPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Unique
    private void hexAutomata$generalPreTP(Vec3 newPos) {
        CommonEventsHandler.trigger(BuiltinEventMarker.TELEPORT.INSTANCE, ServerPlayer.class.cast(this), new IHAEvent.CommonTeleport(position(), newPos));
    }

    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"))
    public void onTeleport(double x, double y, double z, CallbackInfo ci) {
        hexAutomata$generalPreTP(new Vec3(x, y, z));
    }
    @Inject(method = "teleportRelative", at = @At("HEAD"))
    public void onTeleportRelative(double dx, double dy, double dz, CallbackInfo ci) {
        hexAutomata$generalPreTP(position().add(dx, dy, dz));
    }
}
