package io.yukkuric.hexautomata.helpers

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.casting.MishapConstMessage

// resloc
val ADV_SELF_EXPOSED = HexAutomata.modLoc("self_exposed")
val ADV_ROOT = HexAutomata.modLoc("root")
val ADV_STACK_OVERFLOW = HexAutomata.modLoc("stack_overflow")

// mishaps
val MISHAP_PLAYER_ADV_GATE = MishapConstMessage("hexcasting.message.cant_great_spell".asTranslatedComponent)
val MISHAP_PLAYER_BRAINSWEEP_INVALID_TARGET =
    MishapConstMessage("mishap.hexautomata.player_brainsweep.not_target".asTranslatedComponent)
val MISHAP_BEACON_INACTIVE = MishapConstMessage("mishap.hexautomata.beacon_inactive".asTranslatedComponent)