package io.yukkuric.hexautomata.network.packet

import at.petrak.hexcasting.common.msgs.IMessage
import io.yukkuric.hexautomata.HexAutomata
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Rotation
import vazkii.patchouli.api.PatchouliAPI

data class S2CShowMultiblock(
    val id: ResourceLocation, val blockPos: BlockPos, val rotation: Rotation, val message: Component
) : IMessage {
    companion object {
        @JvmStatic
        val ID = HexAutomata.modLoc("show_multiblock")

        fun deserialize(buf: FriendlyByteBuf): S2CShowMultiblock {
            val id = buf.readResourceLocation()
            val pos = buf.readBlockPos()
            val rot = Rotation.values()[buf.readByte().toInt()]
            val msg = buf.readComponent()
            return S2CShowMultiblock(id, pos, rot, msg)
        }

        fun handle(packet: S2CShowMultiblock) {
            val api = PatchouliAPI.get()
            val ritual = api.getMultiblock(packet.id) ?: return
            api.showMultiblock(ritual, packet.message, packet.blockPos, packet.rotation)
        }
    }

    override fun serialize(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(id)
        buf.writeBlockPos(blockPos)
        buf.writeByte(rotation.ordinal)
        buf.writeComponent(message)
    }

    override fun getFabricId() = ID
}
