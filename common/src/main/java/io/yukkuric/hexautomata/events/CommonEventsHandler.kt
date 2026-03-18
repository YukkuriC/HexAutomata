package io.yukkuric.hexautomata.events

import io.yukkuric.hexautomata.items.ItemReactiveFocus
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.server.level.ServerPlayer

object CommonEventsHandler {
    private fun generalTrigger(type: EventMarker, player: ServerPlayer, event: IHAEvent) {
        for (stack in FocusCollector.getAllFocus(player, type)) {
            return (stack.item as ItemReactiveFocus).runCallback(stack, event, player)
        }
    }

    private val CACHED_HANDLERS = HashMap<EventMarker, (ServerPlayer, IHAEvent) -> Any?>()

    @JvmStatic
    operator fun get(marker: EventMarker) = CACHED_HANDLERS.computeIfAbsent(marker) {
        fun(player: ServerPlayer, event: IHAEvent) =
            generalTrigger(marker, player, event)
    }
}