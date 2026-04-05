package io.yukkuric.hexautomata.actions

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import io.yukkuric.hexautomata.HexAutomata.modLoc
import io.yukkuric.hexautomata.helpers.CustomRegisterObject

object HAActions : CustomRegisterObject<ActionRegistryEntry>() {
    init {
        wrap("quantum_swap", "aqaedwaqded", HexDir.NORTH_WEST, OpQuantumSwap)
        wrap("event/write", "aqaedwaqeeeeed", HexDir.NORTH_WEST, OpEventWrite)
        wrap("event/read", "aqqqqqedwaqded", HexDir.EAST, OpEventRead)
    }

    private fun wrap(name: String, signature: String, dir: HexDir, action: Action?): ActionRegistryEntry {
        val pattern = HexPattern.fromAngles(signature, dir)
        val key = modLoc(name)
        val entry = ActionRegistryEntry(pattern, action)
        this[key] = entry
        return entry
    }
}