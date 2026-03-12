package io.yukkuric.hexautomata.blocks

import io.yukkuric.hexautomata.register
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.jetbrains.annotations.NotNull

class BrainsweepIntermediate : Block(PROP_BLOCK), EntityBlock {
    companion object {
        private val PROP_BLOCK = Properties.of().noCollission().noLootTable()
        private val BLOCKS = HashMap<ResourceLocation, BrainsweepIntermediate>()
        private val BE_TYPES = HashMap<ResourceLocation, BlockEntityType<*>>()

        fun registerBlocks(r: (ResourceLocation, Block) -> Any?) = BLOCKS.register(r)
        fun registerBETypes(r: (ResourceLocation, BlockEntityType<*>) -> Any?) = BE_TYPES.register(r)

        fun create(id: ResourceLocation) {
            val block = BrainsweepIntermediate()
            BLOCKS[id] = block
            val type = BEType(block)
            BE_TYPES[id] = type
        }
    }

    override fun getRenderShape(blockState: BlockState?) = RenderShape.INVISIBLE

    override fun asItem(): Item {
        val id = BuiltInRegistries.BLOCK.getKey(this)
        return BuiltInRegistries.ITEM[id]
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BE {
        val id = BuiltInRegistries.BLOCK.getKey(this)
        val type = BuiltInRegistries.BLOCK_ENTITY_TYPE[id]!!
        return BE(type, blockPos, blockState)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ) = blockEntityType as? BlockEntityTicker<T>

    class BEType(src: BrainsweepIntermediate) : BlockEntityType<BE>(src::newBlockEntity, setOf(src), null),
        BlockEntityTicker<BE> {
        override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: BE) {
            // destroy self
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3)
            // spawn item
            val id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this)
            val item = BuiltInRegistries.ITEM[id]
            val e = ItemEntity(EntityType.ITEM, level)
            e.item = ItemStack(item)
            e.setPos(blockPos.center)
            e.setDeltaMovement(0.0, 0.2, 0.0)
            level.addFreshEntity(e)
        }
    }

    class BE(type: BlockEntityType<*>, pos: @NotNull BlockPos, state: BlockState) : BlockEntity(type, pos, state)
}
