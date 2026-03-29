package io.yukkuric.hexautomata.mixin.hex;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep;
import io.yukkuric.hexautomata.HexAutomata;
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep$Spell")
public abstract class MixinBrainsweep {
    @Shadow(remap = false)
    public abstract BlockPos getPos();
    @Shadow(remap = false)
    public abstract Mob getSacrifice();

    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At("RETURN"), remap = false)
    void hookPostBrainsweep(CastingEnvironment env, CallbackInfo ci) {
        var level = env.getWorld();
        var sacrifice = getSacrifice();
        var be = level.getBlockEntity(getPos());
        if (be instanceof BrainsweepIntermediate.BE iBE) {
            iBE.setSacrifice(sacrifice);
        }
    }
}
