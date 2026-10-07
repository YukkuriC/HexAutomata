package io.yukkuric.hexautomata.network

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity


object HAPackets {
    var CLIENT: Client? = null
    var SERVER: Server? = null

    interface IKeepAlive {
        fun keepAlive() {}
    }

    interface Client : IKeepAlive {
        fun sendPacketToServer(packet: CustomPacketPayload)
    }

    interface Server : IKeepAlive {
        fun sendPacketToPlayer(player: ServerPlayer, packet: CustomPacketPayload)
        fun sendPacketTracking(entity: Entity, packet: CustomPacketPayload)
        fun sendPacketToPlayerAndTracking(player: ServerPlayer, packet: CustomPacketPayload) {
            sendPacketToPlayer(player, packet)
            sendPacketTracking(player, packet)
        }
    }
}
