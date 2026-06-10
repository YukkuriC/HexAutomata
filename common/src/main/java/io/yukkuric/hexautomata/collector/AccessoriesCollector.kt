// 生成于 GLM-5V-Turbo
package io.yukkuric.hexautomata.collector

import io.wispforest.accessories.api.AccessoriesCapability
import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

object AccessoriesCollector : FocusCollector() {
    override fun enabled() = HAConfig.EnablesFocusInsideAccessories()
    override fun extract(player: ServerPlayer): Sequence<ItemStack> {
        val cap = AccessoriesCapability.get(player) ?: return sequenceOf()
        val slots = cap.allEquipped
        return sequence {
            for (i in 0 until slots.size) {
                val stack = slots[i].stack
                yield(stack)
            }
        }
    }
}
