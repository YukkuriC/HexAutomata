package io.yukkuric.hexautomata.blocks

import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.helpers.CustomRegisterObject
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object HABlocks : CustomRegisterObject<Block>() {
    object BETypes : CustomRegisterObject<BlockEntityType<*>>()

    @JvmStatic
    fun getBlock(id: ResourceLocation) = this[id]
    @JvmStatic
    fun getBlock(id: String) = this[HexAutomata.modLoc(id)]
    @JvmStatic
    fun getBEType(id: ResourceLocation) = BETypes[id]
    @JvmStatic
    fun getBEType(id: String) = BETypes[HexAutomata.modLoc(id)]

    fun <T : Block, U : BlockEntity> createBE(id: ResourceLocation, block: T, type: BlockEntityType<U>) =
        Pair(set(id, block), BETypes.set(id, type))
}