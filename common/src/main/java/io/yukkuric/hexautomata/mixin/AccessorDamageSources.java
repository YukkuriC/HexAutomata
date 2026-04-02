package io.yukkuric.hexautomata.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DamageSources.class)
public interface AccessorDamageSources {
    @Invoker
    DamageSource callSource(ResourceKey<DamageType> var1, Entity var2);
}
