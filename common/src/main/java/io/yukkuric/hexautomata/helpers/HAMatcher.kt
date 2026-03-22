package io.yukkuric.hexautomata.helpers

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import vazkii.patchouli.api.IStateMatcher
import vazkii.patchouli.api.TriPredicate

abstract class HAMatcher : IStateMatcher,
    TriPredicate<BlockGetter, BlockPos, BlockState> {
    override fun getStatePredicate() = this
}