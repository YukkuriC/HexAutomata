package io.yukkuric.hexautomata.forge.collector

import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.CuriosApi

object CuriosCollector : FocusCollector() {
    override fun extract(player: ServerPlayer): Sequence<ItemStack>? {
        if (!HAConfig.EnablesFocusInsideAccessories()) return null
        val api = CuriosApi.getCuriosHelper().getEquippedCurios(player).orElse(null) ?: return null
        return sequence {
            for (i in 0 until api.slots) {
                val stack = api.getStackInSlot(i)
                yield(stack)
            }
        }
    }
}