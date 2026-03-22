package io.yukkuric.hexautomata.multiblock.matchers

import io.yukkuric.hexautomata.helpers.HAMatcher
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

open class MultiMatcher(vararg val blocks: Block) : HAMatcher() {
    override fun getDisplayedState(ticks: Long) = blocks[((ticks / 20) % blocks.size).toInt()].defaultBlockState()
    override fun test(block: BlockGetter, pos: BlockPos, state: BlockState) = blocks.any(state::`is`)
}