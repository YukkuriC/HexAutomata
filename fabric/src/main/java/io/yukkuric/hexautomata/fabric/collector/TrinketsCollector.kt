package io.yukkuric.hexautomata.fabric.collector

import dev.emi.trinkets.api.TrinketsApi
import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

object TrinketsCollector : FocusCollector() {
    override fun enabled() = HAConfig.EnablesFocusInsideAccessories()
    override fun extract(player: ServerPlayer): Sequence<ItemStack> {
        val api = TrinketsApi.getTrinketComponent(player).orElse(null) ?: return sequenceOf()
        return api.allEquipped.asSequence().map { pair -> pair.b }
    }
}