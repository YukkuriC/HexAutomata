package io.yukkuric.hexautomata.items.collector

import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.events.EventMarker
import io.yukkuric.hexautomata.items.ItemFocusBundle
import io.yukkuric.hexautomata.items.ItemReactiveFocus
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

abstract class FocusCollector {
    companion object {
        private val MAP = LinkedHashMap<String, FocusCollector>()

        init {
            MAP["inv"] = Inv
        }

        @JvmStatic
        fun filterSeq(raw: Sequence<ItemStack>, type: EventMarker): Sequence<ItemStack> = sequence {
            for (stack in raw) when (val item = stack.item) {
                is ItemReactiveFocus -> if (item.isListening(stack, type)) yield(stack)
                is ItemFocusBundle -> yieldAll(filterSeq(item.getContentsSequence(stack), type))
            }
        }
        @JvmStatic
        fun getAllFocus(player: ServerPlayer, type: EventMarker): Sequence<ItemStack> {
            return sequence {
                for (getter in MAP.values) {
                    val raw = getter.extract(player) ?: continue
                    yieldAll(filterSeq(raw, type))
                }
            }
        }
        @JvmStatic
        fun register(id: String, obj: FocusCollector): FocusCollector {
            MAP.put(id, obj) ?: return obj
            throw IllegalArgumentException("duplicate focus collector id: $id")
        }
    }

    abstract fun extract(player: ServerPlayer): Sequence<ItemStack>?

    object Inv : FocusCollector() {
        override fun extract(player: ServerPlayer) =
            if (HAConfig.EnablesFocusInsideInventory()) player.inventory.items.asSequence() else null
    }
}