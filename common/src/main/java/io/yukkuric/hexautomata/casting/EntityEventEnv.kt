package io.yukkuric.hexautomata.casting

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import io.yukkuric.hexautomata.events.IHAEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.Vec3

class EntityEventEnv(
    val event: IHAEvent,
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
}