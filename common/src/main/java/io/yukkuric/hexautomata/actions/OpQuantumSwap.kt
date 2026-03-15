package io.yukkuric.hexautomata.actions

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import io.yukkuric.hexautomata.casting.EntityEventEnv
import io.yukkuric.hexautomata.events.IHAEvent

object OpQuantumSwap : EventLimitedAction() {
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 2

    override fun execute(args: List<Iota>, env: EntityEventEnv): List<Iota> {
        val caster = env.castingEntity ?: return listOf()
        val target = env.event.entity ?: caster
        var oldPosTarget = target.position()
        env.event.let { it as IHAEvent.CommonProjHit }?.let {
            oldPosTarget = it.extraAmbitCenter()
        }
        val oldPosCaster = caster.position()
        caster.teleportToWithTicket(oldPosTarget.x, oldPosTarget.y, oldPosTarget.z)
        if (target != caster) target.teleportTo(oldPosCaster.x, oldPosCaster.y, oldPosCaster.z)
        return listOf()
    }
}