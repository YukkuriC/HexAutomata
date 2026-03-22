package io.yukkuric.hexautomata.multiblock.matchers

import io.yukkuric.hexautomata.helpers.HAMatcher
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

open class DisplayMatcher(val forShow: Block) : HAMatcher() {
    override fun getDisplayedState(ticks: Long) = forShow.defaultBlockState()
    override fun test(block: BlockGetter, pos: BlockPos, state: BlockState) = !state.isAir
}