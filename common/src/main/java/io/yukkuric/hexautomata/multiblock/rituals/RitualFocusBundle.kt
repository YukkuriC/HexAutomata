package io.yukkuric.hexautomata.multiblock.rituals

import at.petrak.hexcasting.common.lib.HexBlocks
import com.google.common.base.Suppliers
import io.yukkuric.hexautomata.multiblock.matchers.DisplayMatcher
import io.yukkuric.hexautomata.multiblock.matchers.MultiMatcher
import io.yukkuric.hexautomata.multiblock.matchers.TagMatcher
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import vazkii.patchouli.api.IMultiblock
import vazkii.patchouli.api.IStateMatcher
import vazkii.patchouli.common.multiblock.SparseMultiblock
import vazkii.patchouli.common.multiblock.StateMatcher

object RitualFocusBundle : RitualCollector() {
    override val idStr = "focus_bundle"
    override val ritual = Suppliers.memoize {
        fun isMenger(step: Int, vararg axes: Int): Boolean {
            var hollowCount = 0
            for (a in axes) if ((a / step) == 1) hollowCount++
            if (hollowCount >= 2) return false
            if (step <= 1) return true
            return isMenger(step / 3, *axes.map { it % step }.toIntArray())
        }

        val matchers = HashMap<BlockPos, IStateMatcher>()
        for (x in 0 until 9)
            for (y in 0 until 9)
                for (z in 0 until 9)
                    if (isMenger(3, x, y, z)) matchers[BlockPos(x, y, z)] = matcherBody
        val matcherRecord = StateMatcher.fromBlockLoose(HexBlocks.AKASHIC_RECORD)
        for (delta in -2..2 step 4) {
            matchers[BlockPos(4, 0, 4 + delta)] = matcherRecord
            matchers[BlockPos(4 + delta, 0, 4)] = matcherRecord
        }
        val beaconBase = TagMatcher(BlockTags.BEACON_BASE_BLOCKS)
        for (x in 3..5)
            for (z in 3..5)
                matchers[BlockPos(x, 0, z)] = beaconBase
        matchers[BlockPos(4, 1, 4)] = DisplayMatcher(Blocks.BEACON)
        SparseMultiblock(matchers).setOffset(4, 1, 4).setSymmetrical(true)
    }
    override val postEffect = { level: ServerLevel, pos: BlockPos, ritual: IMultiblock, rotation: Rotation ->
        for (record in ritual.simulate(level, pos, rotation, false).second) {
            when (record.stateMatcher) {
                null -> {}
                matcherBody -> if (Math.random() < 0.5) {
                    var shiftPos: BlockPos? = null
                    for (i in 1 until (4 + (Math.random() * 4).toInt())) {
                        val newShift =
                            (shiftPos ?: record.worldPosition).offset(Direction.getRandom(level.random).normal)
                        if (level.getBlockState(newShift).isAir) shiftPos = newShift
                        else break
                    }
                    if (shiftPos == null) {
                        level.setBlock(
                            record.worldPosition,
                            DECAYED_BLOCK_SET.random().defaultBlockState().rotate(Rotation.getRandom(level.random)),
                            3
                        )
                    } else {
                        level.setBlock(
                            shiftPos,
                            (if (Math.random() < 0.05) DECAYED_BLOCK_SET_RARE else DECAYED_BLOCK_SET).random()
                                .defaultBlockState().rotate(Rotation.getRandom(level.random)),
                            3
                        )
                        level.setBlock(record.worldPosition, Blocks.AIR.defaultBlockState(), 3)
                    }
                }

                else -> level.setBlock(record.worldPosition, Blocks.AIR.defaultBlockState(), 3)
            }
        }
    }

    private val matcherBody = MultiMatcher(HexBlocks.AKASHIC_LIGATURE, HexBlocks.AKASHIC_BOOKSHELF)
    var DECAYED_BLOCK_SET = mutableListOf(
        HexBlocks.AMETHYST_BRICKS,
        HexBlocks.QUENCHED_ALLAY_BRICKS,
        HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL,
        HexBlocks.EDIFIED_PLANKS,
        Blocks.AMETHYST_BLOCK,
        HexBlocks.AKASHIC_BOOKSHELF,
        HexBlocks.AKASHIC_LIGATURE,
    )
    var DECAYED_BLOCK_SET_RARE = mutableListOf(
        HexBlocks.AKASHIC_RECORD,
        Blocks.SHULKER_BOX,
        HexBlocks.QUENCHED_ALLAY,
        Blocks.BUDDING_AMETHYST,
        Blocks.DIAMOND_BLOCK,
    )
}