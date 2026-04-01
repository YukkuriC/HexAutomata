package io.yukkuric.hexautomata.actions.patch

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Blocks

object OpExtendBrainsweep : PatchAction(OpBrainsweep, BrainsweepEx) {
    object BrainsweepEx : ConstMediaAction {
        override val argc = 2
        override val mediaCost = 1000000L

        override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
            val entity = args.getEntity(0, argc)
            if (entity is ServerPlayer) {
                val pos = args.getBlockPos(1, argc)
                env.assertPosInRangeForEditing(pos)
                env.world.setBlock(pos, Blocks.BUDDING_AMETHYST.defaultBlockState(), 3)
                return listOf()
            }

            throw USE_ORIGINAL
        }
    }
}