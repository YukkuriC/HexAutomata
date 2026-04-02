package io.yukkuric.hexautomata.fabric

import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.common.msgs.IMessage
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomata.IAPI
import io.yukkuric.hexautomata.HexAutomata.commonInit
import io.yukkuric.hexautomata.HexAutomata.commonLateInit
import io.yukkuric.hexautomata.HexAutomataClient
import io.yukkuric.hexautomata.actions.HAActions
import io.yukkuric.hexautomata.blocks.HABlocks
import io.yukkuric.hexautomata.fabric.events.HAFabricEventsListener
import io.yukkuric.hexautomata.fabric.interop.TrinketsInterop
import io.yukkuric.hexautomata.items.HAItems
import io.yukkuric.hexautomata.network.HAPackets
import io.yukkuric.hexautomata.network.packet.S2CShowMultiblock
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Mob

class HexAutomataFabric : IAPI(), ModInitializer {
    private fun <T : Any> bindReg(reg: Registry<T>, loader: ((ResourceLocation, T) -> Any?) -> Any?) {
        loader { k, v -> Registry.register(reg, k, v) }
    }

    override fun onInitialize() {
        bindReg(HexActions.REGISTRY, HAActions::register)
        bindReg(BuiltInRegistries.ITEM, HAItems::register)
        bindReg(BuiltInRegistries.CREATIVE_MODE_TAB, HAItems.Tabs::register)
        bindReg(BuiltInRegistries.BLOCK, HABlocks::register)
        bindReg(BuiltInRegistries.BLOCK_ENTITY_TYPE, HABlocks.BETypes::register)
        HAFabricEventsListener.load()
        HexAutomata.tryLoadInterop("trinkets", TrinketsInterop::run)
        commonInit()

        var lateInitOnce = false
        ServerLifecycleEvents.SERVER_STARTING.register {
            if (lateInitOnce) return@register
            lateInitOnce = true
            commonLateInit()
        }
    }

    override fun modLoaded(id: String) = FabricLoader.getInstance().isModLoaded(id)
    override fun revertBrainsweep(mob: Mob) {
        val comp = HexCardinalComponents.BRAINSWEPT.get(mob)
        comp.isBrainswept = false
        forceRefresh(mob)
    }

    companion object {
        init {
            HAConfigFabric.setup()
        }
    }

    object Network : HAPackets.Server {
        init {
            HAPackets.SERVER = this
        }

        private fun <T> make(
            decoder: (FriendlyByteBuf) -> T, handle: (T, ServerPlayer) -> Unit
        ) = ServerPlayNetworking.PlayChannelHandler { _, player: ServerPlayer, _, buf: FriendlyByteBuf, _ ->
            handle(decoder(buf), player)
        }

        override fun sendPacketToPlayer(player: ServerPlayer, packet: IMessage) {
            ServerPlayNetworking.send(player, packet.fabricId, packet.toBuf())
        }
    }
}

class HexAutomataFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        HexAutomataClient.load()
    }

    object Network : HAPackets.Client {
        init {
            HAPackets.CLIENT = this
            ClientPlayNetworking.registerGlobalReceiver(
                S2CShowMultiblock.ID, make(
                    S2CShowMultiblock::deserialize, S2CShowMultiblock::handle
                )
            )
        }

        private fun <T> make(
            decoder: (FriendlyByteBuf) -> T, handler: (T) -> Unit
        ) = ClientPlayNetworking.PlayChannelHandler { _, _, buf, _ -> handler(decoder(buf)) }

        override fun sendPacketToServer(packet: IMessage) {
            ClientPlayNetworking.send(packet.fabricId, packet.toBuf())
        }
    }
}