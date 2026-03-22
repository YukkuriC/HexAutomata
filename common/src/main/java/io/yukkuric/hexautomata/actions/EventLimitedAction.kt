package io.yukkuric.hexautomata.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import io.yukkuric.hexautomata.casting.EntityEventEnv
import io.yukkuric.hexautomata.casting.MishapOutEvent
import net.minecraft.world.entity.LivingEntity

abstract class EventLimitedAction : ConstMediaAction {
    override fun operate(
        env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation
    ): OperationResult {
        if (env !is EntityEventEnv) {
            // punish caster
            env.castingEntity?.let { doQuantumPunish(it) }

            // break continuation
            return OperationResult(
                image,
                listOf(OperatorSideEffect.DoMishap(MishapOutEvent, Mishap.Context(null, null))),
                SpellContinuation.Done,
                HexEvalSounds.MISHAP,
            )
        }
        return super.operate(env, image, continuation)
    }

    override fun execute(args: List<Iota>, env: CastingEnvironment) = execute(args, env as EntityEventEnv)
    abstract fun execute(args: List<Iota>, env: EntityEventEnv): List<Iota>

    open fun doQuantumPunish(caster: LivingEntity) {
        val lst = mutableListOf(caster.x, caster.y, caster.z)
        lst.shuffle()
        for (i in 0 until 3) if (Math.random() < 0.5) lst[i] *= -1.0
        caster.teleportToWithTicket(lst[0], lst[1], lst[2])
    }
}