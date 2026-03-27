package io.yukkuric.hexautomata.casting

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughMedia
import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.events.IHAEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

class EntityEventEnv(
    val event: IHAEvent,
    val stack: ItemStack,
    caster: ServerPlayer,
    castingHand: InteractionHand = InteractionHand.MAIN_HAND
) : PlayerBasedCastEnv(caster, castingHand) {
    override fun extractMediaEnvironment(cost: Long, simulate: Boolean) =
        if (isCreativeMode) 0L else extractMediaFromInventory(cost, canOvercast(), simulate)

    override fun getCastingHand() = castingHand
    override fun getPigment() = HexAPI.instance().getColorizer(this.caster)

    override fun isVecInRangeEnvironment(vec: Vec3): Boolean {
        val ret = super.isVecInRangeEnvironment(vec)
        if (ret) return true
        for (extraCenter in event.extraAmbitCenters())
            if (extraCenter.distanceTo(vec) <= 1) return true
        return false
    }

    fun executeIotasWithTax(iotas: List<Iota>) {
        val image = CastingImage().copy(stack = event.initStack())
        val vm = CastingVM(image, this)

        val tax = HAConfig.EventTriggerTax().toLong()
        if (tax > 0) {
            val taxLeft = extractMedia(tax, false)
            if (taxLeft > 0) {
                val mishap = MishapNotEnoughMedia(tax)
                val effect = OperatorSideEffect.DoMishap(mishap, Mishap.Context(null, null))
                return effect.performEffect(vm)
            }
        }

        vm.queueExecuteAndWrapIotas(iotas, caster.serverLevel())
    }
}