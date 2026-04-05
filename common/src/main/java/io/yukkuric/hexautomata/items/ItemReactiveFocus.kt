package io.yukkuric.hexautomata.items

import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.items.storage.ItemFocus
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.casting.EntityEventEnv
import io.yukkuric.hexautomata.events.EventMarker
import io.yukkuric.hexautomata.events.IHAEvent
import io.yukkuric.hexautomata.helpers.TooltipHelper
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class ItemReactiveFocus(val type: EventMarker, props: Properties) : ItemFocus(props) {
    constructor(type: EventMarker) : this(type, HAItems.Props.STACK_ONE)

    open fun isListening(stack: ItemStack, marker: EventMarker) = type == marker

    open fun runCallback(stack: ItemStack, event: IHAEvent, player: ServerPlayer): Boolean {
        val spell = readIota(stack, player.serverLevel()) ?: return false
        val list = if (spell is ListIota) spell.list.toList() else listOf(spell)
        EntityEventEnv(event, stack, player).executeIotasWithTax(list)
        return true
    }

    override fun getName(focus: ItemStack): Component {
        val nameEvent = Language.getInstance()
            .getOrDefault("block.hexautomata.reactive_focus.${type.name.lowercase()}", type.name.capitalize())
        return Component.translatable("item.hexautomata.reactive_focus.template", nameEvent)
    }

    override fun appendHoverText(
        stack: ItemStack,
        lvl: Level?,
        tooltips: MutableList<Component?>,
        advanced: TooltipFlag
    ) {
        TooltipHelper.appendScopeTooltip(tooltips)
        super.appendHoverText(stack, lvl, tooltips, advanced)
    }

    companion object {
        val DATA_PRED: ResourceLocation = HexAutomata.modLoc("data")
    }
}