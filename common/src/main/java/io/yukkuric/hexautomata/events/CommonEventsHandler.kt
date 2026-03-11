package io.yukkuric.hexautomata.events

import io.yukkuric.hexautomata.items.ItemReactiveFocus
import net.minecraft.server.level.ServerPlayer

object CommonEventsHandler {
    private fun generalTrigger(type: EventMarker, player: ServerPlayer, event: IHAEvent) {
        for (stack in player.inventory.items) {
            val item = stack.item
            if (item !is ItemReactiveFocus || !item.isListening(stack, type)) continue
            return item.runCallback(stack, event, player)
        }
    }

    private val CACHED_HANDLERS = HashMap<EventMarker, (ServerPlayer, IHAEvent) -> Any?>()

    @JvmStatic
    operator fun get(marker: EventMarker) = CACHED_HANDLERS.computeIfAbsent(marker) {
        fun(player: ServerPlayer, event: IHAEvent) =
            generalTrigger(marker, player, event)
    }
}