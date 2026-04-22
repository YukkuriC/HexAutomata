package io.yukkuric.hexautomata.forge

import at.petrak.hexcasting.forge.xplat.ForgeXplatImpl.TAG_BRAINSWEPT
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomataClient
import io.yukkuric.hexautomata.forge.events.HAForgeEventsListener
import io.yukkuric.hexautomata.network.HAPackets
import io.yukkuric.hexautomata.network.packet.S2CPlayerExposureEffect
import io.yukkuric.hexautomata.network.packet.S2CShowMultiblock
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadHandler

@Mod(HexAutomata.MOD_ID)
class HexAutomataForge(modContainer: ModContainer) : HexAutomata.IAPI() {
    init {
        HAForgeEventsListener.load(modContainer)
        HAConfigForge.register(modContainer)
        // HexAutomata.tryLoadInterop("curios", CuriosInterop::run)
        Network.init(modContainer.eventBus!!)
    }

    override fun modLoaded(id: String) = ModList.get().isLoaded(id)
    override fun revertBrainsweep(mob: Mob) {
        mob.persistentData.remove(TAG_BRAINSWEPT)
        forceRefresh(mob)
    }

    @Suppress("INACCESSIBLE_TYPE")
    object Network : HAPackets.Client, HAPackets.Server {
        const val PROTOCOL_VERSION: String = "1"
        fun init(modBus: IEventBus) {
            HAPackets.CLIENT = this
            HAPackets.SERVER = this

            modBus.addListener(RegisterPayloadHandlersEvent::class.java, { e ->
                val reg = e.registrar(PROTOCOL_VERSION)
                // server show patchouli multiblock
                reg.playToClient(
                    S2CShowMultiblock.TYPE,
                    S2CShowMultiblock.STREAM_CODEC,
                    makeS2C(S2CShowMultiblock::handle)
                )
                reg.playToClient(
                    S2CPlayerExposureEffect.TYPE,
                    S2CPlayerExposureEffect.STREAM_CODEC,
                    makeS2C(S2CPlayerExposureEffect::handle)
                )
            })
        }

        private fun <T : CustomPacketPayload?> makeS2C(consumer: (T) -> Unit) =
            IPayloadHandler<T> { m, ctx ->
                consumer(m)
            }

        override fun sendPacketToServer(packet: CustomPacketPayload) =
            PacketDistributor.sendToServer(packet)

        override fun sendPacketToPlayer(player: ServerPlayer, packet: CustomPacketPayload) =
            PacketDistributor.sendToPlayer(player, packet)

        override fun sendPacketTracking(entity: Entity, packet: CustomPacketPayload) {
            PacketDistributor.sendToPlayersTrackingEntity(entity, packet)
        }

        override fun sendPacketToPlayerAndTracking(player: ServerPlayer, packet: CustomPacketPayload) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet)
        }
    }
}

object HexAutomataForgeClient {
    @SubscribeEvent
    fun OnClientInit(e: FMLClientSetupEvent) {
        HexAutomataClient.load()
    }
}