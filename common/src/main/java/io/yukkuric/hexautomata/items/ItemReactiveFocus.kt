package io.yukkuric.hexautomata.items

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.items.storage.ItemFocus
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.casting.EntityEventEnv
import io.yukkuric.hexautomata.events.EventMarker
import io.yukkuric.hexautomata.events.IHAEvent
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

class ItemReactiveFocus(val type: EventMarker, props: Properties) : ItemFocus(props) {
    constructor(type: EventMarker) : this(type, HAItems.Props.STACK_ONE)

    open fun isListening(stack: ItemStack, marker: EventMarker) = type == marker

    open fun runCallback(stack: ItemStack, event: IHAEvent, player: ServerPlayer) {
        val spell = readIota(stack, player.serverLevel()) ?: return
        val list = if (spell is ListIota) spell.list.toList() else listOf(spell)

        val env = EntityEventEnv(event, player)
        val image = CastingImage().copy(stack = event.initStack())
        CastingVM(image, env).queueExecuteAndWrapIotas(list, player.serverLevel())
    }

    override fun getName(focus: ItemStack): Component {
        val nameEvent = Language.getInstance()
            .getOrDefault("block.hexautomata.reactive_focus.${type.name.lowercase()}", type.name.capitalize())
        return Component.translatable("item.hexautomata.reactive_focus.template", nameEvent)
    }

    companion object {
        val DATA_PRED: ResourceLocation = HexAutomata.modLoc("data")
    }
}