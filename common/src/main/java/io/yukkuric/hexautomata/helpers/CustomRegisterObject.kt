package io.yukkuric.hexautomata.helpers

import io.yukkuric.hexautomata.HexAutomata
import net.minecraft.resources.ResourceLocation

open class CustomRegisterObject<T> {
    private val _MAP = HashMap<ResourceLocation, T>()
    operator fun get(key: String) = this[HexAutomata.modLoc(key)]
    operator fun get(key: ResourceLocation) = _MAP[key]
    operator fun set(key: ResourceLocation, obj: T): T {
        val old = _MAP.put(key, obj)
        if (old != null) throw IllegalArgumentException("duped id $key in type $javaClass")
        return obj
    }

    val entries get() = _MAP.entries
}