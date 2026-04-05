package io.yukkuric.hexautomata.mixin.hex;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ActionRegistryEntry.class)
public interface AccessorActionRegistryEntry {
    @Mutable
    @Accessor(remap = false)
    void setAction(Action v);
}
