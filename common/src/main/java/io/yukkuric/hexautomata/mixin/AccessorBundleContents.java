package io.yukkuric.hexautomata.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin({BundleContents.class, BundleContents.Mutable.class})
public interface AccessorBundleContents {
    @Accessor
    List<ItemStack> getItems();
}
