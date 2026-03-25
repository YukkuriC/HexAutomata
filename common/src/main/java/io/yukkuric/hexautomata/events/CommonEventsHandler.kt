package io.yukkuric.hexautomata.events

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedMishapEnv
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.casting.MishapOutEvent
import io.yukkuric.hexautomata.items.ItemReactiveFocus
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.server.level.ServerPlayer

object CommonEventsHandler {
    @JvmStatic
    fun trigger(type: EventMarker, player: ServerPlayer, event: IHAEvent) {
        if (CommonHelpers.trackRecursive(player)) {
            // TODO: advnacements?

            // manually call mishap
            val mishapEnv = PlayerBasedMishapEnv(player)
            mishapEnv.drown()
            player.sendSystemMessage(MishapOutEvent.ERROR_MSG)
            return
        }
        try {
            for (stack in FocusCollector.getAllFocus(player, type)) {
                (stack.item as ItemReactiveFocus).runCallback(stack, event, player)
                if (HAConfig.FirstFocusOnly()) return
            }
            CommonHelpers.releaseRecursive(player)
        } catch (e: Throwable) {
            if (e !is Mishap) HexAutomata.LOGGER.error(e.toString())
        }
    }
}