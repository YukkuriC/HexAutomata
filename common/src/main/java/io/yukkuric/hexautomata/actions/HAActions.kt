package io.yukkuric.hexautomata.actions

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import io.yukkuric.hexautomata.HexAutomata.modLoc
import io.yukkuric.hexautomata.register
import net.minecraft.resources.ResourceLocation

class HAActions {
    companion object {
        private val CACHED: MutableMap<ResourceLocation, ActionRegistryEntry> = HashMap()

        init {
            wrap("quantum_swap", "aqaedwaqded", HexDir.NORTH_WEST, OpQuantumSwap)
            wrap("event/write", "aqaedwaqeeeeed", HexDir.NORTH_WEST, OpEventWrite)
            wrap("event/read", "aqqqqqedwaqded", HexDir.EAST, OpEventRead)
        }

        @JvmStatic
        fun registerActions(regFunc: (ResourceLocation, ActionRegistryEntry) -> Any?) = CACHED.register(regFunc)

        private fun wrap(name: String, signature: String, dir: HexDir, action: Action?): ActionRegistryEntry {
            val pattern = HexPattern.fromAngles(signature, dir)
            val key = modLoc(name)
            val entry = ActionRegistryEntry(pattern, action)
            CACHED[key] = entry
            return entry
        }
    }
}