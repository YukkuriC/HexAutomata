package io.yukkuric.hexautomata.blocks

import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.register
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object HABlocks {
    private val BLOCKS = HashMap<ResourceLocation, Block>()
    private val BE_TYPES = HashMap<ResourceLocation, BlockEntityType<*>>()
    @JvmStatic
    fun getBlock(id: ResourceLocation) = BLOCKS[id]
    @JvmStatic
    fun getBlock(id: String) = BLOCKS[HexAutomata.modLoc(id)]
    @JvmStatic
    fun getBEType(id: ResourceLocation) = BE_TYPES[id]
    @JvmStatic
    fun getBEType(id: String) = BE_TYPES[HexAutomata.modLoc(id)]

    fun registerBlocks(r: (ResourceLocation, Block) -> Any?) = BLOCKS.register(r)
    fun registerBETypes(r: (ResourceLocation, BlockEntityType<*>) -> Any?) = BE_TYPES.register(r)

    private fun <T : Block> createBlock(id: ResourceLocation, block: T): T {
        BLOCKS[id] = block
        return block
    }

    private fun <T : BlockEntity> createBEType(id: ResourceLocation, type: BlockEntityType<T>): BlockEntityType<T> {
        BE_TYPES[id] = type
        return type
    }

    fun <T : Block, U : BlockEntity> createBE(id: ResourceLocation, block: T, type: BlockEntityType<U>) =
        Pair(createBlock(id, block), createBEType(id, type))
}