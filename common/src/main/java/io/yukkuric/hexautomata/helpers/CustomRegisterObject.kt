package io.yukkuric.hexautomata.helpers

import io.yukkuric.hexautomata.HexAutomata
import net.minecraft.resources.ResourceLocation

open class CustomRegisterObject<T> {
    val MAP = LinkedHashMap<ResourceLocation, T>()
    operator fun get(key: String) = this[HexAutomata.modLoc(key)]
    operator fun get(key: ResourceLocation) = MAP[key]
    operator fun set(key: ResourceLocation, obj: T): T {
        val old = MAP.put(key, obj)
        if (old != null) throw IllegalArgumentException("duped id $key in type $javaClass")
        return obj
    }

    fun register(regFunc: (ResourceLocation, T) -> Any?) {
        for (pair in MAP.entries) regFunc(pair.key, pair.value)
    }
}