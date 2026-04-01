package io.yukkuric.hexautomata.events

import at.petrak.hexcasting.api.casting.mishaps.Mishap
import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.grantAdvancement
import io.yukkuric.hexautomata.items.ItemReactiveFocus
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.server.level.ServerPlayer

object CommonEventsHandler {
    @JvmStatic
    fun trigger(type: EventMarker, player: ServerPlayer, event: IHAEvent) {
        if (CommonHelpers.trackRecursive(player)) return
        try {
            for (stack in FocusCollector.getAllFocus(player, type)) {
                val hit = (stack.item as ItemReactiveFocus).runCallback(stack, event, player)
                if (hit) {
                    player.grantAdvancement(HexAutomata.modLoc("root"))
                    if (HAConfig.FirstFocusOnly()) return
                }
            }
            CommonHelpers.releaseRecursive(player)
        } catch (e: Throwable) {
            if (e !is Mishap) HexAutomata.LOGGER.error(e.toString())
        }
    }
}