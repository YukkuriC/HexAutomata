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
import io.yukkuric.hexautomata.helpers.*
import io.yukkuric.hexautomata.network.HAPackets
import io.yukkuric.hexautomata.network.packet.S2CPlayerExposureEffect
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.TicketType
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.ChunkPos
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

    fun gateAdvancement(player: ServerPlayer) {
        if (!player.hasAdvancement(ADV_SELF_EXPOSED)) throw MISHAP_PLAYER_ADV_GATE
    }

    val PLAYER_TO_ENTITY = createPlayerCallback(EntityIota.TYPE) { player, data, env ->
        // expose self
        if (data.entity == player) return@createPlayerCallback Result.build(
            {
                if (!player.hasAdvancement(ADV_SELF_EXPOSED)) player.grantAdvancement(ADV_SELF_EXPOSED)
                env.printMessage("advancement.hexautomata:self_exposed.desc".asTranslatedComponent.lightPurple)
                player.addEffect(MobEffectInstance(MobEffects.LEVITATION, 50))
                player.addEffect(MobEffectInstance(MobEffects.SLOW_FALLING, 100))
                // env.world.broadcastEntityEvent(player, 35.toByte())
                HAPackets.SERVER?.sendPacketToPlayerAndTracking(player, S2CPlayerExposureEffect(player))
            },
            10 * MediaConstants.CRYSTAL_UNIT,
            ParticleSpray.cloud(player.position(), 1.0),
            ParticleSpray.cloud(player.eyePosition, 1.0),
        )

        // advancement gate
        gateAdvancement(player)

        // connect to other entity
        // TODO
        throw MISHAP_PLAYER_BRAINSWEEP_INVALID_TARGET
    }
    val PLAYER_TO_BLOCK = createPlayerCallback(Vec3Iota.TYPE) { player, iota, env ->
        // advancement gate
        gateAdvancement(player)

        val world = env.world
        val pos = BlockPos.containing(iota.vec3)
        val isChunkLoaded = world.isLoaded(pos)
        val state = env.world.getBlockState(pos)
        val block = state.block
        when (block) {
            // teleporting to beacon beam
            Blocks.BEACON -> {
                // check beacon active
                val be = world.getBlockEntity(pos)
                if (isChunkLoaded)
                    (be as? BeaconBlockEntity)?.let {
                        if (it.beamSections.isEmpty()) throw MISHAP_BEACON_INACTIVE
                    }
                else {
                    world.chunkSource.addRegionTicket(TicketType.PORTAL, ChunkPos(pos), 1, pos)
                    throw MISHAP_BEACON_INACTIVE
                }

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
        throw MISHAP_PLAYER_BRAINSWEEP_INVALID_TARGET
    }
}
