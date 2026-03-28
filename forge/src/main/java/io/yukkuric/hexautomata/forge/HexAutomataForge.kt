package io.yukkuric.hexautomata.forge

import at.petrak.hexcasting.common.msgs.IMessage
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomataClient
import io.yukkuric.hexautomata.forge.events.HAForgeEventsListener
import io.yukkuric.hexautomata.forge.interop.CuriosInterop
import io.yukkuric.hexautomata.network.HAPackets
import io.yukkuric.hexautomata.network.packet.S2CShowMultiblock
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.BiConsumer
import java.util.function.Supplier

@Mod(HexAutomata.MOD_ID)
class HexAutomataForge : HexAutomata.IAPI() {
    init {
        HAForgeEventsListener.load()
        HAConfigForge.register(ModLoadingContext.get())
        HexAutomata.tryLoadInterop("curios", CuriosInterop::run)
        Network // hook init
    }

    override fun modLoaded(id: String) = ModList.get().isLoaded(id)

    object Network : HAPackets.Client, HAPackets.Server {
        const val PROTOCOL_VERSION: String = "1"
        val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
            HexAutomata.modLoc("network"), { PROTOCOL_VERSION }, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
        )

        init {
            HAPackets.CLIENT = this
            HAPackets.SERVER = this

            // packets
            var idx = 0

            // clipboard
            // server show patchouli multiblock
            CHANNEL.registerMessage(
                idx++,
                S2CShowMultiblock::class.java,
                S2CShowMultiblock::serialize,
                S2CShowMultiblock::deserialize,
                makeS2C(S2CShowMultiblock::handle)
            )
        }

        private fun <T> makeS2C(consumer: (T) -> Unit) =
            BiConsumer { packet: T, ctx: Supplier<NetworkEvent.Context> ->
                consumer(packet)
                ctx.get().packetHandled = true
            }

        override fun sendPacketToServer(packet: IMessage) = CHANNEL.sendToServer(packet)

        override fun sendPacketToPlayer(player: ServerPlayer, packet: IMessage) =
            CHANNEL.send(PacketDistributor.PLAYER.with { player }, packet)
    }
}

object HexAutomataForgeClient {
    @SubscribeEvent
    fun OnClientInit(e: FMLClientSetupEvent) {
        HexAutomataClient.load()
    }
}