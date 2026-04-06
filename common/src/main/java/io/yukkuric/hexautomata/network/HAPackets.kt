package io.yukkuric.hexautomata.network

import at.petrak.hexcasting.common.msgs.IMessage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity


object HAPackets {
    var CLIENT: Client? = null
    var SERVER: Server? = null

    interface Client {
        fun sendPacketToServer(packet: IMessage)
    }

    interface Server {
        fun sendPacketToPlayer(player: ServerPlayer, packet: IMessage)
        fun sendPacketTracking(entity: Entity, packet: IMessage)
        fun sendPacketToPlayerAndTracking(player: ServerPlayer, packet: IMessage) {
            sendPacketToPlayer(player, packet)
            sendPacketTracking(player, packet)
        }
    }
}
