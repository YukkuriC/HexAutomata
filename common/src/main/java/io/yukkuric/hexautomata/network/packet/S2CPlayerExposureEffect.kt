package io.yukkuric.hexautomata.network.packet

import at.petrak.hexcasting.common.msgs.IMessage
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.items.HAItems
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity

data class S2CPlayerExposureEffect(val entityID: Int) : IMessage {
    constructor(entity: Entity) : this(entity.id)

    companion object {
        @JvmStatic
        val ID = HexAutomata.modLoc("player_exposure")

        private val LENS = lazy { HAItems.LOGO.defaultInstance }

        fun deserialize(buf: FriendlyByteBuf) = S2CPlayerExposureEffect(buf.readInt())

        fun handle(packet: S2CPlayerExposureEffect) {
            val mc = Minecraft.getInstance()
            val level = mc.level ?: return
            val entity = level.getEntity(packet.entityID) ?: return

            mc.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30)
            level.playLocalSound(
                entity.x, entity.y, entity.z, SoundEvents.TOTEM_USE, entity.soundSource, 1.0f, 1.0f, false
            )
            if (entity === mc.player) mc.gameRenderer.displayItemActivation(LENS.value)
        }
    }

    override fun serialize(buf: FriendlyByteBuf) {
        buf.writeInt(entityID)
    }

    override fun getFabricId() = ID
}