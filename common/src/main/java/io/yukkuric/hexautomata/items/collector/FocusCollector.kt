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
            MAP["hands"] = Hands
            MAP["inv"] = Inv
            MAP["ender_chest"] = EnderChest
        }

        @JvmStatic
        fun entries() = MAP.entries
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
                    if (!getter.enabled()) continue
                    val raw = getter.extract(player)
                    yieldAll(filterSeq(raw, type))
                }
            }
        }
        fun register(id: String, obj: FocusCollector): FocusCollector {
            MAP.put(id, obj) ?: return obj
            throw IllegalArgumentException("duplicate focus collector id: $id")
        }
    }

    abstract fun enabled(): Boolean
    abstract fun extract(player: ServerPlayer): Sequence<ItemStack>

    object Inv : FocusCollector() {
        override fun enabled() = HAConfig.EnablesFocusInsideInventory()
        override fun extract(player: ServerPlayer) = player.inventory.items.asSequence()
    }

    object Hands : FocusCollector() {
        override fun enabled() = HAConfig.EnablesFocusInHands() && !HAConfig.EnablesFocusInsideInventory()
        override fun extract(player: ServerPlayer) = sequenceOf(player.mainHandItem, player.offhandItem)
    }

    object EnderChest : FocusCollector() {
        override fun enabled() = HAConfig.EnablesFocusInsideEnderChest()
        override fun extract(player: ServerPlayer) = sequence {
            val inv = player.enderChestInventory
            for (i in 0 until inv.containerSize) yield(inv.getItem(i))
        }
    }
}