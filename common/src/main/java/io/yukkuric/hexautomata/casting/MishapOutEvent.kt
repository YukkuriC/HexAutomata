package io.yukkuric.hexautomata.casting

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.TreeList
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object MishapOutEvent : Mishap() {
    override fun fillInStackTrace() = this
    private fun readResolve(): Any = MishapOutEvent
    val ERROR_MSG = Component.translatable("mishap.hexautomata.out_event").withStyle(ChatFormatting.DARK_AQUA)
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context) =
        FrozenPigment.DEFAULT.get() // TODO: where's ancient pigment?

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) = ERROR_MSG
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: TreeList<Iota>): TreeList<Iota> {
        val mutable = stack.toMutableList()
        for (i in 0 until stack.size) {
            if (Math.random() < 0.5) mutable[i] = GarbageIota()
        }
        return TreeList.from(mutable)
    }
}