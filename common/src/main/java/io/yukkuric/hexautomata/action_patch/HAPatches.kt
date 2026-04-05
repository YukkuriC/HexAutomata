package io.yukkuric.hexautomata.action_patch

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.common.lib.hex.HexActions
import io.yukkuric.hexautomata.action_patch.brainsweep.OpExtendBrainsweep
import io.yukkuric.hexautomata.helpers.CustomRegisterObject
import io.yukkuric.hexautomata.mixin.hex.AccessorActionRegistryEntry

object HAPatches : CustomRegisterObject<Action>() {
    init {
        this[HexAPI.modLoc("brainsweep")] = OpExtendBrainsweep
    }

    fun patchAll() {
        for (pair in MAP.entries) {
            val entry =
                HexActions.REGISTRY[pair.key] ?: throw IllegalArgumentException("invalid patch for id ${pair.key}")
            (entry as AccessorActionRegistryEntry).setAction(pair.value)
        }
    }
}