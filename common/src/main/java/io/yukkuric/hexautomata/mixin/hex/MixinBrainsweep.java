package io.yukkuric.hexautomata.mixin.hex;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.EntityTagIngredient;
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediate;
import io.yukkuric.hexautomata.helpers.HelpersExtKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep$Spell")
public abstract class MixinBrainsweep {
    @Shadow(remap = false)
    public abstract BlockPos getPos();
    @Shadow(remap = false)
    public abstract Mob getSacrifice();

    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At("RETURN"), remap = false)
    void hookPostBrainsweep(CastingEnvironment env, CallbackInfo ci) {
        HelpersExtKt.tryRecordBrainsweepSacrifice(env.getWorld(), getPos(), getSacrifice());
    }

    @Mixin(EntityTagIngredient.class)
    static class EntityTagIngredientDisplay {
        @Shadow(remap = false)
        @Final
        public TagKey<EntityType<?>> entityTypeTag;
        @Inject(method = "exampleEntity", at = @At("HEAD"), remap = false, cancellable = true)
        void loopedEntityList(Level level, CallbackInfoReturnable<Entity> cir) {
            var optional = BuiltInRegistries.ENTITY_TYPE.getTag(entityTypeTag);
            if (optional.isEmpty()) {
                cir.setReturnValue(null);
                return;
            }
            var allEntities = optional.get();
            int tick = (int) (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) % allEntities.size());
            cir.setReturnValue(allEntities.get(tick).value().create(level));
        }
    }
}
