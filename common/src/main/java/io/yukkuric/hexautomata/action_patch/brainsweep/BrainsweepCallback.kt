package io.yukkuric.hexautomata.action_patch.brainsweep

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexautomata.action_patch.brainsweep.callbacks.SelfExposureCallback
import io.yukkuric.hexautomata.helpers.SinglePutMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

abstract class BrainsweepCallback<E : Entity, I : Iota>(
    var priority: Int,
    val limitEntity: EntityType<E>?,
    val limitIota: IotaType<I>?,
) {
    /**
     * - return null for check fail
     * - throw exceptions for stronger rejection
     * - editing OpExtendBrainsweep.resultStack for extra data flow
     */
    abstract fun call(entity: E, iota: I, env: CastingEnvironment): SpellAction.Result?

    data class ResultAction(val action: (CastingEnvironment) -> Unit) : RenderedSpell {
        override fun cast(env: CastingEnvironment) = action(env)
    }

    companion object : SinglePutMap<String, BrainsweepCallback<*, *>>() {
        private val _cacheSorted = HashMap<Pair<EntityType<*>?, IotaType<*>?>, List<BrainsweepCallback<*, *>>>()
        private var _keySetLoaded = false


        init {
            SelfExposureCallback.loadAll()
        }

        @JvmStatic
        override fun setChanged() {
            _cacheSorted.clear()
            _keySetLoaded = false
        }

        @JvmStatic
        fun buildResult(action: (CastingEnvironment) -> Unit, cost: Long, vararg particles: ParticleSpray) =
            SpellAction.Result(ResultAction(action), cost, particles.toList())

        @JvmStatic
        fun callAll(entity: Entity, iota: Iota, env: CastingEnvironment): SpellAction.Result? {
            val entityType: EntityType<*> = entity.type
            val iotaType: IotaType<*> = iota.type

            val callbacks = _cacheSorted.computeIfAbsent(Pair(entityType, iotaType)) {
                MAP.values.filter {
                    it.limitEntity?.let { if (it != entityType) return@filter false }
                    it.limitIota?.let { if (it != iotaType) return@filter false }
                    return@filter true
                }.sortedBy { it.priority }
            }
            for (c in callbacks) (c as BrainsweepCallback<Entity, Iota>).call(entity, iota, env)?.let { return it }
            return null
        }

        // ====== KubeJS interop ======
        @JvmStatic
        fun createRaw(
            priority: Int,
            et: EntityType<*>?,
            it: IotaType<*>?,
            callback: (entity: Entity, iota: Iota, env: CastingEnvironment) -> SpellAction.Result?
        ) = object : BrainsweepCallback<Entity, Iota>(priority, et as EntityType<Entity>?, it as IotaType<Iota>?) {
            override fun call(entity: Entity, iota: Iota, env: CastingEnvironment) = callback(entity, iota, env)
        }

        @JvmStatic
        fun create(
            priority: Int,
            etid: ResourceLocation?,
            itid: ResourceLocation?,
            callback: (entity: Entity, iota: Iota, env: CastingEnvironment) -> SpellAction.Result?
        ): BrainsweepCallback<Entity, Iota> {
            val et = etid?.let { BuiltInRegistries.ENTITY_TYPE.get(etid) }
            val it = itid?.let {
                val wrapId = if (it.namespace == "minecraft") ResourceLocation("hexcasting", it.path) else it
                HexIotaTypes.REGISTRY.get(wrapId)
            }
            return createRaw(priority, et, it, callback)
        }

        @JvmStatic
        override fun forceSet(key: String, obj: BrainsweepCallback<*, *>) = super.forceSet(key, obj)
    }
}
