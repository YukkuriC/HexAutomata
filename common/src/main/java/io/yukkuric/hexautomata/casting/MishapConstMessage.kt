package io.yukkuric.hexautomata.casting

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

data class MishapConstMessage(val msg: Component) : Mishap() {
    override fun fillInStackTrace() = this
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.GREEN)
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) = msg
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}
