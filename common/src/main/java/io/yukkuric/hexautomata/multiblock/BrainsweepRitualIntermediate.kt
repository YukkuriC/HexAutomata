package io.yukkuric.hexautomata.multiblock

import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry
import at.petrak.hexcasting.common.recipe.ingredient.StateIngredientBlock
import com.google.common.base.Suppliers
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediate
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import vazkii.patchouli.api.PatchouliAPI

class BrainsweepRitualIntermediate : BrainsweepIntermediate() {
    companion object {
        fun create(id: ResourceLocation): RitualBEType {
            val block = BrainsweepRitualIntermediate()
            BLOCKS[id] = block
            val type = RitualBEType(id, block)
            BE_TYPES[id] = type
            return type
        }
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BE {
        val id = BuiltInRegistries.BLOCK.getKey(this)
        val type = BuiltInRegistries.BLOCK_ENTITY_TYPE[id]!!
        return BE(type, blockPos, blockState)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level, blockState: BlockState, blockEntityType: BlockEntityType<T>
    ) = blockEntityType as? BlockEntityTicker<T>

    // leave it mutable for modifications (like modpacks)
    class RitualBEType(var ritualId: ResourceLocation, src: BrainsweepRitualIntermediate) : BEType(src),
        BlockEntityTicker<BE> {
        override fun perform() {
            val ritual = PatchouliAPI.get().getMultiblock(ritualId)
            val rot = ritual?.validate(level, blockPos)
            if (rot == null) {
                destroySelf(fallbackBlock.get() ?: Blocks.DIRT)
                val center = blockPos.center
                val explosion = Explosion(
                    level, null, center.x, center.y, center.z, 5f, false, Explosion.BlockInteraction.KEEP
                )
                explosion.explode()
                if (ritual != null) {
                    PatchouliAPI.get().showMultiblock(
                        ritual,
                        Component.translatable("hexautomata.ritual.missing"),
                        blockPos.offset(0, -1, 0),
                        Rotation.NONE
                    )
                }
                return
            }
            super.perform()
            HARituals.PostRitualEffects[ritualId]?.let { it(level, blockPos, ritual, rot) }
        }

        // default: pick source block from recipe
        var fallbackBlock = Suppliers.memoize {
            for (recipe in level.recipeManager.getAllRecipesFor(HexRecipeStuffRegistry.BRAINSWEEP_TYPE)) {
                if (recipe.result.block != src) continue
                val blockIn = recipe.blockIn
                if (blockIn is StateIngredientBlock) {
                    return@memoize blockIn.block
                }
            }
            return@memoize null
        }
    }
}
