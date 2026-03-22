package io.yukkuric.hexautomata.multiblock.rituals

import at.petrak.hexcasting.common.lib.HexBlocks
import com.google.common.base.Suppliers
import io.yukkuric.hexautomata.multiblock.matchers.DisplayMatcher
import io.yukkuric.hexautomata.multiblock.matchers.MultiMatcher
import io.yukkuric.hexautomata.multiblock.matchers.TagMatcher
import net.minecraft.core.BlockPos
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
        for (offset in RECORD_POS) level.setBlock(pos.offset(offset), Blocks.AIR.defaultBlockState(), 3)
        for (dx in -1..1) for (dz in -1..1) level.setBlock(
            pos.offset(dx, -1, dz), Blocks.AIR.defaultBlockState(), 3
        )
    }

    private val matcherBody = MultiMatcher(HexBlocks.AKASHIC_LIGATURE, HexBlocks.AKASHIC_BOOKSHELF)
    val RECORD_POS = listOf(BlockPos(0, -1, 2), BlockPos(0, -1, -2), BlockPos(2, -1, 0), BlockPos(-2, -1, 0))
}