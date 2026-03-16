package io.yukkuric.hexautomata.fabric.collector

import dev.emi.trinkets.api.TrinketsApi
import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

object TrinketsCollector : FocusCollector() {
    override fun extract(player: ServerPlayer): Sequence<ItemStack>? {
        if (!HAConfig.EnablesFocusInsideAccessories()) return null
        val api = TrinketsApi.getTrinketComponent(player).orElse(null) ?: return null
        return api.allEquipped.asSequence().map { pair -> pair.b }
    }
}