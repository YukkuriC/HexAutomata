package io.yukkuric.hexautomata.events

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.items.ItemReactiveFocus
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.server.level.ServerPlayer

object CommonEventsHandler {
    private fun generalTrigger(type: EventMarker, player: ServerPlayer, event: IHAEvent) {
        try {
            for (stack in FocusCollector.getAllFocus(player, type)) {
                val ret = (stack.item as ItemReactiveFocus).runCallback(stack, event, player)
                if (HAConfig.FirstFocusOnly()) return ret
            }
        } catch (e: Throwable) {
            player.sendSystemMessage(("hexcasting.mishap.unknown").asTranslatedComponent(e))
        }
    }

    private val CACHED_HANDLERS = HashMap<EventMarker, (ServerPlayer, IHAEvent) -> Any?>()

    @JvmStatic
    operator fun get(marker: EventMarker) = CACHED_HANDLERS.computeIfAbsent(marker) {
        fun(player: ServerPlayer, event: IHAEvent) =
            generalTrigger(marker, player, event)
    }
}