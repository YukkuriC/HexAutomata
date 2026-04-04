package io.yukkuric.hexautomata.action_patch.brainsweep

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.helpers.SinglePutMap
import io.yukkuric.hexautomata.helpers.grantAdvancement
import io.yukkuric.hexautomata.helpers.hasAdvancement
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

object BrainsweepCallback : SinglePutMap<Pair<EntityType<*>, IotaType<*>>, BrainsweepCallbackEntry<*, *>>() {
    fun <E : Entity, I : Iota> create(
        te: EntityType<E>, ti: IotaType<I>,
        callbackFunc: (E, I, CastingEnvironment) -> Unit,
        costFunc: ((E, I, CastingEnvironment) -> Long)? = null,
        validFunc: ((E, I, CastingEnvironment) -> Boolean)? = null,
    ): BrainsweepCallbackEntry<E, I> {
        val ret = BrainsweepCallbackEntry(callbackFunc, costFunc, validFunc)
        this[Pair(te, ti)] = ret
        return ret
    }

    private val LOC_SELF_EXPOSED = HexAutomata.modLoc("self_exposed")
    val PLAYER_TO_ENTITY = create(EntityType.PLAYER, EntityIota.TYPE, { player, data, env ->
        if (player != env.castingEntity) throw MishapOthersName(player)
        val sp = player as ServerPlayer

        // expose self
        if (!sp.hasAdvancement(LOC_SELF_EXPOSED)) {
            if (data.entity == sp) sp.grantAdvancement(LOC_SELF_EXPOSED)
            return@create
        }

        // connect to other entity
        // TODO
    })
}

data class BrainsweepCallbackEntry<E : Entity, I : Iota>(
    var callbackFunc: (E, I, CastingEnvironment) -> Unit,
    var costFunc: ((E, I, CastingEnvironment) -> Long)? = null,
    var validFunc: ((E, I, CastingEnvironment) -> Boolean)? = null,
) {
    fun cast() = this as BrainsweepCallbackEntry<Entity, Iota>
    fun run(e: E, i: I, env: CastingEnvironment) = callbackFunc(e, i, env)
    fun cost(e: E, i: I, env: CastingEnvironment) = costFunc?.let { it(e, i, env) } ?: 0L
    fun isValid(e: E, i: I, env: CastingEnvironment) = validFunc?.let { it(e, i, env) } != false
}