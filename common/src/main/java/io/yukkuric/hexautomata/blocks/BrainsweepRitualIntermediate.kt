package io.yukkuric.hexautomata.blocks

import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry
import at.petrak.hexcasting.common.recipe.ingredient.StateIngredientBlock
import com.google.common.base.Suppliers
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.multiblock.HARituals
import io.yukkuric.hexautomata.network.HAPackets
import io.yukkuric.hexautomata.network.packet.S2CShowMultiblock
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import vazkii.patchouli.api.PatchouliAPI

// leave it mutable for modifications (like modpacks)
open class BrainsweepRitualIntermediate(var ritualId: ResourceLocation) : BrainsweepIntermediate() {
    companion object {
        fun create(id: ResourceLocation) =
            BrainsweepRitualIntermediate(id).let { HABlocks.createBE(id, it, BEType(it)) }

        val MSG_RITUAL_MISSING = Component.translatable("hexautomata.ritual.missing")
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BE {
        val id = BuiltInRegistries.BLOCK.getKey(this)
        val type = BuiltInRegistries.BLOCK_ENTITY_TYPE[id]!!
        return BE(type, blockPos, blockState)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level, blockState: BlockState, blockEntityType: BlockEntityType<T>
    ) = this as BlockEntityTicker<T>

    override fun perform() {
        val ritual = PatchouliAPI.get().getMultiblock(ritualId)
        val rot = ritual?.validate(level, blockPos)
        if (rot == null) {
            destroySelf(fallbackBlock.get() ?: Blocks.DIRT)
            val center = blockPos.center

            // revert brainsweep
            (sacrifice as? Mob)?.let(HexAutomata.API::revertBrainsweep)

            if (ritual != null) {
                val displayCenter = blockPos.offset(0, -1, 0)
                for (player in level.getPlayers { it.position().distanceTo(center) < 32 }) {
                    HAPackets.sendPacketToPlayer(
                        player, S2CShowMultiblock(
                            ritualId, displayCenter, Rotation.NONE, MSG_RITUAL_MISSING
                        )
                    )
                }
            }
            return
        }
        super.perform()
        HARituals.PostRitualEffects[ritualId]?.let { it(level, blockPos, ritual, rot) }
    }

    // default: pick source block from recipe
    var fallbackBlock = Suppliers.memoize {
        for (recipe in level.recipeManager.getAllRecipesFor(HexRecipeStuffRegistry.BRAINSWEEP_TYPE)) {
            if (recipe.result.block != this) continue
            val blockIn = recipe.blockIn
            if (blockIn is StateIngredientBlock) {
                return@memoize blockIn.block
            }
        }
        return@memoize null
    }
}
