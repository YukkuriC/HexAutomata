package io.yukkuric.hexautomata.helpers

import io.yukkuric.hexautomata.HexAutomata
import net.minecraft.resources.ResourceLocation

open class CustomRegisterObject<T> : SinglePutMap<ResourceLocation, T>() {
    operator fun get(key: String) = this[HexAutomata.modLoc(key)]

    fun register(regFunc: (ResourceLocation, T) -> Any?) {
        for (pair in MAP.entries) regFunc(pair.key, pair.value)
    }
}