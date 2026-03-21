package io.yukkuric.hexautomata.items

import at.petrak.hexcasting.api.utils.putList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BundleItem
import net.minecraft.world.item.ItemStack
import kotlin.math.min

class ItemFocusBundle : BundleItem(HAItems.Props.STACK_ONE_EPIC) {
    companion object {
        val MAX_FOCUS_COUNT = 8
        val KEY_ITEMS = "Items"
        private val STUB_LIST = ListTag()

        @JvmStatic
        fun pushOne(bundleStack: ItemStack, otherStack: ItemStack) {
            val listTag = bundleStack.listTag(true)
            val dump = CompoundTag()
            otherStack.save(dump)
            listTag.add(dump)
        }
        @JvmStatic
        fun popOne(bundleStack: ItemStack): ItemStack? {
            val listTag = bundleStack.listTag(false)
            if (listTag.isEmpty()) return null
            val pop = listTag.removeAt(0) as CompoundTag
            return ItemStack.of(pop)
        }

        private fun ItemStack.listTag(create: Boolean = false): ListTag {
            val tag = if (create) this.orCreateTag else this.tag ?: return STUB_LIST
            if (!tag.contains(KEY_ITEMS)) {
                if (!create) return STUB_LIST
                tag.putList(KEY_ITEMS, ListTag())
            }
            return tag.getList(KEY_ITEMS, Tag.TAG_COMPOUND.toInt())
        }

        private fun ItemStack.getFocusCount() = this.listTag().size
        private fun ItemStack.isFull() = this.getFocusCount() >= MAX_FOCUS_COUNT
    }

    override fun overrideStackedOnOther(
        bundleStack: ItemStack, bundleSlot: Slot, clickAction: ClickAction, player: Player
    ): Boolean {
        if (clickAction != ClickAction.SECONDARY) return false
        val otherStack = bundleSlot.item
        if (otherStack.isEmpty) {
            popOne(bundleStack)?.let(bundleSlot::set)
        } else if (otherStack.item is ItemReactiveFocus && !bundleStack.isFull()) {
            pushOne(bundleStack, otherStack)
            otherStack.count = 0
        }
        return true
    }

    override fun overrideOtherStackedOnMe(
        bundleStack: ItemStack,
        otherStack: ItemStack,
        bundleSlot: Slot,
        clickAction: ClickAction,
        player: Player,
        mouseSlot: SlotAccess
    ): Boolean {
        if (clickAction != ClickAction.SECONDARY || !bundleSlot.allowModification(player)) return false
        if (otherStack.isEmpty) {
            popOne(bundleStack)?.let(mouseSlot::set)
        } else if (otherStack.item is ItemReactiveFocus && !bundleStack.isFull()) {
            pushOne(bundleStack, otherStack)
            otherStack.count = 0
        }
        return true
    }

    fun getContentsSequence(stack: ItemStack) =
        Sequence(stack.listTag()::iterator).map { tag -> ItemStack.of(tag as CompoundTag) }

    override fun isBarVisible(stack: ItemStack) = stack.getFocusCount() > 0
    override fun getBarWidth(stack: ItemStack) = min((1 + 12 * stack.getFocusCount() / MAX_FOCUS_COUNT), 13)
}