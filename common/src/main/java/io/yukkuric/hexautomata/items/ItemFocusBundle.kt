package io.yukkuric.hexautomata.items

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.gray
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.helpers.TooltipHelper
import io.yukkuric.hexautomata.mixin.AccessorBundleContents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BundleItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.BundleContents
import net.minecraft.world.level.Level
import kotlin.math.min

class ItemFocusBundle : BundleItem(HAItems.Props.STACK_ONE_EPIC) {
    companion object {
        val MAX_FOCUS_COUNT = 8
        val KEY_ITEMS = "Items"
        private val STUB_LIST = listOf<ItemStack>()
        private val STUB_CONTENTS = BundleContents(STUB_LIST)

        fun pushOne(bundleStack: ItemStack, otherStack: ItemStack) {
            val contents = bundleStack.get(DataComponents.BUNDLE_CONTENTS) ?: STUB_CONTENTS
            val mutable = BundleContents.Mutable(contents)
            (mutable as AccessorBundleContents).items.add(otherStack.copy())
            bundleStack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable())
        }

        fun popOne(bundleStack: ItemStack): ItemStack? {
            val contents = bundleStack.get(DataComponents.BUNDLE_CONTENTS) ?: return null
            val mutable = BundleContents.Mutable(contents)
            val items = (mutable as AccessorBundleContents).items
            if (items.isEmpty()) return null
            val ret = items.removeLast()
            bundleStack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable())
            return ret
        }

        private fun ItemStack.listTag(): List<ItemStack> {
            val contents = get(DataComponents.BUNDLE_CONTENTS) ?: return STUB_LIST
            return (contents as AccessorBundleContents).items
        }

        private fun ItemStack.getFocusCount() = this.listTag().size
        private fun ItemStack.isFull() = this.getFocusCount() >= MAX_FOCUS_COUNT

        val CONTENTS_PRED: ResourceLocation = HexAutomata.modLoc("contents")

        object Client {
            fun contentsPredicate(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, i: Int): Float {
                return stack.getFocusCount().toFloat()
            }
        }
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        // TBD, but not dropping all
        return InteractionResultHolder.pass(player.getItemInHand(interactionHand))
    }

    override fun appendHoverText(
        stack: ItemStack,
        tooltipContext: TooltipContext,
        tooltips: MutableList<Component?>,
        advanced: TooltipFlag
    ) {
        TooltipHelper.appendScopeTooltip(tooltips)
        // super.appendHoverText(stack, lvl, tooltips, advanced)
        tooltips.add(
            "item.minecraft.bundle.fullness".asTranslatedComponent(
                stack.getFocusCount(),
                MAX_FOCUS_COUNT
            ).gray
        )
        for (focus in getContentsSequence(stack)) tooltips.add(focus.item.getName(focus))
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
        Sequence(stack.listTag()::iterator)

    override fun isBarVisible(stack: ItemStack) = stack.getFocusCount() > 0
    override fun getBarWidth(stack: ItemStack) = min((1 + 12 * stack.getFocusCount() / MAX_FOCUS_COUNT), 13)
    override fun getBarColor(itemStack: ItemStack): Int = (0xff00ffee).toInt()
}