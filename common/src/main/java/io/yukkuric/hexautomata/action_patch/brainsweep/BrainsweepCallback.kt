package io.yukkuric.hexautomata.action_patch.brainsweep

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import io.yukkuric.hexautomata.action_patch.brainsweep.callbacks.SelfExposureCallback
import io.yukkuric.hexautomata.helpers.SinglePutMap
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

abstract class BrainsweepCallback<E : Entity, I : Iota>(
    var priority: Int,
    val limitEntity: EntityType<E>?,
    val limitIota: IotaType<I>?,
) {
    /**
     * return null for check fail
     * throw exceptions for stronger rejection
     * editing OpExtendBrainsweep for extra data flow
     */
    abstract fun call(entity: E, iota: I, env: CastingEnvironment): SpellAction.Result?

    data class ResultAction(val action: (CastingEnvironment) -> Unit) : RenderedSpell {
        override fun cast(env: CastingEnvironment) = action(env)
    }

    companion object : SinglePutMap<String, BrainsweepCallback<*, *>>() {
        private val _cacheSorted = HashMap<Pair<EntityType<*>, IotaType<*>>, List<BrainsweepCallback<*, *>>>()

        val PLAYER_TO_ENTITY = set("player2entity", SelfExposureCallback.Entity)
        val PLAYER_TO_BLOCK = set("player2pos", SelfExposureCallback.Block)

        override fun setChanged() {
            _cacheSorted.clear()
        }

        @JvmStatic
        fun buildResult(action: (CastingEnvironment) -> Unit, cost: Long, vararg particles: ParticleSpray) =
            SpellAction.Result(ResultAction(action), cost, particles.toList())

        @JvmStatic
        fun callAll(entity: Entity, iota: Iota, env: CastingEnvironment): SpellAction.Result? {
            val callbacks = _cacheSorted.computeIfAbsent(Pair(entity.type, iota.type)) {
                MAP.values.filter {
                    if (it.limitEntity != null && it.limitEntity != entity.type) return@filter false
                    if (it.limitIota != null && it.limitIota != iota.type) return@filter false
                    return@filter true
                }.sortedBy { it.priority }
            }
            for (c in callbacks) (c as BrainsweepCallback<Entity, Iota>).call(entity, iota, env)?.let { return it }
            return null
        }
    }
}
