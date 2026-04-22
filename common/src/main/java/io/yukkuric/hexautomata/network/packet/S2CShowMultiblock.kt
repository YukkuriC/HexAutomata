package io.yukkuric.hexautomata.network.packet

import io.yukkuric.hexautomata.HexAutomata
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Rotation
import vazkii.patchouli.api.PatchouliAPI

data class S2CShowMultiblock(
    val id: ResourceLocation, val blockPos: BlockPos, val rotation: Rotation, val message: Component
) : CustomPacketPayload {
    companion object {
        @JvmStatic
        val ID = HexAutomata.modLoc("show_multiblock")
        val TYPE = CustomPacketPayload.Type<S2CShowMultiblock>(ID)

        val STREAM_CODEC = object : StreamCodec<RegistryFriendlyByteBuf, S2CShowMultiblock> {
            override fun decode(buf: RegistryFriendlyByteBuf) = deserialize(buf)
            override fun encode(buf: RegistryFriendlyByteBuf, packet: S2CShowMultiblock) = packet.serialize(buf)
        }

        fun deserialize(buf: RegistryFriendlyByteBuf): S2CShowMultiblock {
            val id = buf.readResourceLocation()
            val pos = buf.readBlockPos()
            val rot = Rotation.values()[buf.readByte().toInt()]
            val msg = ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf)
            return S2CShowMultiblock(id, pos, rot, msg)
        }

        fun handle(packet: S2CShowMultiblock) {
            val api = PatchouliAPI.get()
            val ritual = api.getMultiblock(packet.id) ?: return
            api.showMultiblock(ritual, packet.message, packet.blockPos, packet.rotation)
        }
    }

    fun serialize(buf: RegistryFriendlyByteBuf) {
        buf.writeResourceLocation(id)
        buf.writeBlockPos(blockPos)
        buf.writeByte(rotation.ordinal)
        message.copy()
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, message)
    }

    override fun type() = TYPE
}
