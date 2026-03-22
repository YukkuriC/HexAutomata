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
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.common.multiblock.AbstractMultiblock

object RitualFocusBundle : RitualCollector() {
    override val idStr = "focus_bundle"
    override val ritual = Suppliers.memoize {
        val l0 = arrayOf(
            "BBBBBBBBB",
            "B_BB_BB_B",
            "BBBBBBBBB",
            "BBB___BBB",
            "B_B___B_B",
            "BBB___BBB",
            "BBBBBBBBB",
            "B_BB_BB_B",
            "BBBBBBBBB",
        )
        val l1 = arrayOf(
            "B_BB_BB_B",
            "_________",
            "B_BB_BB_B",
            "B_B___B_B",
            "_________",
            "B_B___B_B",
            "B_BB_BB_B",
            "_________",
            "B_BB_BB_B",
        )
        val l2 = arrayOf(
            "BBB___BBB",
            "B_B___B_B",
            "BBB___BBB",
            "_________",
            "_________",
            "_________",
            "BBB___BBB",
            "B_B___B_B",
            "BBB___BBB",
        )
        val l3 = arrayOf(
            "B_B___B_B",
            "_________",
            "B_B___B_B",
            "_________",
            "_________",
            "_________",
            "B_B___B_B",
            "_________",
            "B_B___B_B",
        )
        val lb1 = l1.clone()
        lb1[4] = "____T____"
        val lb0 = arrayOf(
            "BBBBBBBBB",
            "B_BB_BB_B",
            "BBBBCBBBB",
            "BBBIIIBBB",
            "B_CI0IC_B",
            "BBBIIIBBB",
            "BBBBCBBBB",
            "B_BB_BB_B",
            "BBBBBBBBB",
        )
        val api = PatchouliAPI.get()
        val beaconBase = TagMatcher(BlockTags.BEACON_BASE_BLOCKS)
        (api.makeMultiblock(
            arrayOf(
                l0, l1, l0, l2, l3, l2, l0, lb1, lb0
            ),
            'B', matcherBody,
            'C', HexBlocks.AKASHIC_RECORD,
            'I', beaconBase,
            '0', beaconBase,
            'T', DisplayMatcher(Blocks.BEACON)
        ) as AbstractMultiblock).setOffset(4, 1, 4)
    }
    override val postEffect = { level: ServerLevel, pos: BlockPos, ritual: IMultiblock, rotation: Rotation ->
        for (record in ritual.simulate(level, pos, rotation, false).second) {
            when (record.stateMatcher) {
                matcherBody -> if (Math.random() < 0.5) {
                    var shiftPos: BlockPos? = null
                    for (i in 1 until (4 + (Math.random() * 4).toInt())) {
                        val newShift =
                            (shiftPos ?: record.worldPosition).offset(Direction.getRandom(level.random).normal)
                        if (level.getBlockState(newShift).isAir) shiftPos = newShift
                        else break
                    }
                    if (shiftPos == null) {
                        level.setBlock(record.worldPosition, DECAYED_BLOCK_SET.random().defaultBlockState(), 3)
                    } else {
                        level.setBlock(
                            shiftPos,
                            (if (Math.random() < 0.05) DECAYED_BLOCK_SET_RARE else DECAYED_BLOCK_SET).random()
                                .defaultBlockState(),
                            3
                        )
                        level.setBlock(record.worldPosition, Blocks.AIR.defaultBlockState(), 3)
                    }
                }

                else -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
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