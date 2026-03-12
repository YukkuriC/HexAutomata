package io.yukkuric.hexautomata

import net.minecraft.resources.ResourceLocation

fun <T> Map<ResourceLocation, T>.register(regFunc: (ResourceLocation, T) -> Any?) {
    for (pair in entries) regFunc(pair.key, pair.value)
}