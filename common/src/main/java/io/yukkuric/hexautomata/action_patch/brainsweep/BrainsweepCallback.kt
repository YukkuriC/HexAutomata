package io.yukkuric.hexautomata.action_patch.brainsweep

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.api.utils.lightPurple
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.helpers.SinglePutMap
import io.yukkuric.hexautomata.helpers.grantAdvancement
import io.yukkuric.hexautomata.helpers.hasAdvancement
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BeaconBlockEntity

typealias BCFunc<E, I> = (E, I, CastingEnvironment) -> SpellAction.Result?

object BrainsweepCallback : SinglePutMap<Pair<EntityType<*>, IotaType<*>>, BCFunc<*, *>>() {
    data class Result(val action: (CastingEnvironment) -> Unit) : RenderedSpell {
        override fun cast(env: CastingEnvironment) = action(env)

        companion object {
            @JvmStatic
            fun build(action: (CastingEnvironment) -> Unit, cost: Long, vararg particles: ParticleSpray) =
                SpellAction.Result(Result(action), cost, particles.toList())
            @JvmStatic
            fun fail(message: Component) = SpellAction.Result(Result({ it.printMessage(message) }), 0, listOf())
        }
    }

    @JvmStatic
    fun <E : Entity, I : Iota> create(
        te: EntityType<E>, ti: IotaType<I>,
        callbackFunc: BCFunc<E, I>,
    ): BCFunc<E, I> {
        this[Pair(te, ti)] = callbackFunc
        return callbackFunc
    }

    @JvmStatic
    fun <I : Iota> createPlayerCallback(
        ti: IotaType<I>,
        playerCallback: BCFunc<ServerPlayer, I>,
    ) = create(EntityType.PLAYER as EntityType<ServerPlayer>, ti) { player, iota, env ->
        if (player != env.castingEntity) throw MishapOthersName(player)
        return@create playerCallback(player, iota, env)
    }

    private val LOC_SELF_EXPOSED = HexAutomata.modLoc("self_exposed")
    private val RESULT_PLAYER_ADV_GATE = Result.fail("hexcasting.message.cant_great_spell".asTranslatedComponent)
    fun gateAdvancement(player: ServerPlayer) =
        if (player.hasAdvancement(LOC_SELF_EXPOSED)) null else RESULT_PLAYER_ADV_GATE

    val PLAYER_TO_ENTITY = createPlayerCallback(EntityIota.TYPE) { player, data, env ->
        // expose self
        if (data.entity == player) return@createPlayerCallback Result.build(
            {
                if (!player.hasAdvancement(LOC_SELF_EXPOSED)) player.grantAdvancement(LOC_SELF_EXPOSED)
                env.printMessage("advancement.hexautomata:self_exposed.desc".asTranslatedComponent.lightPurple)
                player.addEffect(MobEffectInstance(MobEffects.SLOW_FALLING))
            },
            10 * MediaConstants.CRYSTAL_UNIT,
            ParticleSpray.cloud(player.position(), 1.0),
            ParticleSpray.cloud(player.eyePosition, 1.0),
        )

        // advancement gate
        gateAdvancement(player)?.let { return@createPlayerCallback it }

        // connect to other entity
        // TODO
        return@createPlayerCallback null
    }
    val PLAYER_TO_BLOCK = createPlayerCallback(Vec3Iota.TYPE) { player, iota, env ->
        // advancement gate
        gateAdvancement(player)?.let { return@createPlayerCallback it }

        val pos = BlockPos.containing(iota.vec3)
        when (env.world.getBlockState(pos).block) {
            // teleporting to beacon beam
            Blocks.BEACON -> {
                // check beacon active
                val be = env.world.getBlockEntity(pos)
                if ((be as? BeaconBlockEntity)?.beamSections?.isNotEmpty() != true) return@createPlayerCallback Result.fail(
                    "mishap.hexautomata.beacon_inactive".asTranslatedComponent
                )

                val srcFxPos = player.getPosition(player.eyeHeight / 2)
                val target = pos.center.add(0.0, 0.5, 0.0)
                val targetFxPos = target.add(0.0, (player.eyeHeight / 2).toDouble(), 0.0)
                return@createPlayerCallback Result.build(
                    {
                        player.teleportTo(target.x, target.y, target.z)
                    },
                    MediaConstants.CRYSTAL_UNIT,
                    ParticleSpray.cloud(srcFxPos, 2.0),
                    ParticleSpray.burst(targetFxPos, 2.0),
                )
            }
        }
        null
    }
}
