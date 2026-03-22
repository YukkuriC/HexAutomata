package io.yukkuric.hexautomata.helpers

import at.petrak.hexcasting.api.utils.gold
import at.petrak.hexcasting.api.utils.green
import at.petrak.hexcasting.api.utils.white
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.network.chat.Component

object TooltipHelper {
    @JvmStatic
    fun appendScopeTooltip(pTooltipComponents: MutableList<Component?>) {
        var atLeastOne = false
        val tooltip = Component.translatable("tooltip.hexautomata.listeners").white
        for (pair in FocusCollector.entries()) {
            if (!pair.value.enabled()) continue
            if (atLeastOne) tooltip.append(Component.literal(", "))
            tooltip.append(Component.translatable("tooltip.hexautomata.listener.${pair.key}").green)
            atLeastOne = true
        }
        if (!atLeastOne) tooltip.append(Component.translatable("tooltip.hexautomata.listener_none").gold)
        pTooltipComponents.add(tooltip)
        if (!atLeastOne) pTooltipComponents.add(Component.translatable("tooltip.hexautomata.listeners.alert").gold)
    }
}