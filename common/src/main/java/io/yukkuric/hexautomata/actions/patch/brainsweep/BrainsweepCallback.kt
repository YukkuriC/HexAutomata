package io.yukkuric.hexautomata.actions.patch.brainsweep

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import io.yukkuric.hexautomata.helpers.SinglePutMap
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

object BrainsweepCallback : SinglePutMap<Pair<EntityType<*>, IotaType<*>>, BrainsweepCallbackEntry<*, *>>() {
    fun <E : Entity, I : Iota> create(
        te: EntityType<E>, ti: IotaType<I>,
        callbackFunc: (E, I, CastingEnvironment) -> Any?,
        costFunc: ((E, I, CastingEnvironment) -> Long)? = null,
        validFunc: ((E, I, CastingEnvironment) -> Boolean)? = null,
    ): BrainsweepCallbackEntry<E, I> {
        val ret = BrainsweepCallbackEntry(callbackFunc, costFunc, validFunc)
        this[Pair(te, ti)] = ret
        return ret
    }
}

data class BrainsweepCallbackEntry<E : Entity, I : Iota>(
    var callbackFunc: (E, I, CastingEnvironment) -> Any?,
    var costFunc: ((E, I, CastingEnvironment) -> Long)? = null,
    var validFunc: ((E, I, CastingEnvironment) -> Boolean)? = null,
) {
    fun cast() = this as BrainsweepCallbackEntry<Entity, Iota>
    fun run(e: E, i: I, env: CastingEnvironment) = callbackFunc(e, i, env)
    fun cost(e: E, i: I, env: CastingEnvironment) = costFunc?.let { it(e, i, env) } ?: 0L
    fun isValid(e: E, i: I, env: CastingEnvironment) = validFunc?.let { it(e, i, env) } != false
}