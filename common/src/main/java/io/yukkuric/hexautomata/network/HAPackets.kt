package io.yukkuric.hexautomata.network

import at.petrak.hexcasting.common.msgs.IMessage
import net.minecraft.server.level.ServerPlayer


object HAPackets {
    var CLIENT: Client? = null
    var SERVER: Server? = null

    @JvmStatic
    fun sendPacketToServer(packet: IMessage) = CLIENT?.sendPacketToServer(packet)

    @JvmStatic
    fun sendPacketToPlayer(player: ServerPlayer, packet: IMessage) = SERVER?.sendPacketToPlayer(player, packet)

    interface Client {
        fun sendPacketToServer(packet: IMessage)
    }

    interface Server {
        fun sendPacketToPlayer(player: ServerPlayer, packet: IMessage)
    }
}
