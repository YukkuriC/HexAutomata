package io.yukkuric.hexautomata.multiblock.matchers

import io.yukkuric.hexautomata.helpers.HAMatcher
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

open class TagMatcher(val tag: TagKey<Block>) : HAMatcher() {
    override fun getDisplayedState(ticks: Long): BlockState {
        val blocks = BuiltInRegistries.BLOCK.getTagOrEmpty(this.tag).map(Holder<Block>::value).toList()
        if (blocks.isEmpty()) return Blocks.BEDROCK.defaultBlockState()
        return blocks[((ticks / 20) % blocks.size).toInt()].defaultBlockState()
    }

    override fun getStatePredicate() = this
    override fun test(block: BlockGetter, pos: BlockPos, state: BlockState) = state.`is`(tag)
}