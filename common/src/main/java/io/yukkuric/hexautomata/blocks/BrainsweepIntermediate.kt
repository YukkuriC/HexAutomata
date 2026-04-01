package io.yukkuric.hexautomata.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
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
import net.minecraft.world.phys.BlockHitResult
import org.jetbrains.annotations.NotNull
import java.util.*

open class BrainsweepIntermediate : Block(PROP_BLOCK), EntityBlock {
    companion object {
        protected val PROP_BLOCK = Properties.of().noCollission().noLootTable()

        fun create(id: ResourceLocation): Pair<BrainsweepIntermediate, BlockEntityType<BE>> {
            val block = BrainsweepIntermediate()
            val type = BEType(block)
            return HABlocks.createBE(id, block, type)
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

    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        val beType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(BuiltInRegistries.BLOCK.getKey(blockState.block))
        if (beType is BEType) beType.tick(level, blockPos, blockState, null)
        return InteractionResult.PASS
    }

    open class BEType(src: BrainsweepIntermediate) : BlockEntityType<BE>(src::newBlockEntity, setOf(src), null),
        BlockEntityTicker<BE> {

        fun selfId() = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this)

        // contextual data
        lateinit var level: ServerLevel
        lateinit var blockPos: BlockPos
        var self: BE? = null
        var sacrifice: LivingEntity? = null

        @Synchronized
        override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: BE?) {
            if (level.isClientSide) {
                // TODO: make some fx here?
                return
            }
            this.level = level as ServerLevel
            this.blockPos = blockPos
            sacrifice = blockEntity?.sacrifice
            perform()
        }

        open fun perform() {
            // destroy self
            destroySelf()
            // spawn item
            val item = BuiltInRegistries.ITEM[selfId()]
            popItem(ItemStack(item))
        }

        fun destroySelf(newBlock: Block = Blocks.AIR) = level.setBlock(blockPos, newBlock.defaultBlockState(), 3)
        fun popItem(stack: ItemStack) {
            val e = ItemEntity(EntityType.ITEM, level)
            e.item = stack
            e.setPos(blockPos.center)
            e.setDeltaMovement(0.0, 0.2, 0.0)
            level.addFreshEntity(e)
        }
    }

    class BE(type: BlockEntityType<*>, pos: @NotNull BlockPos, state: BlockState) : BlockEntity(type, pos, state) {
        companion object {
            const val KEY_SACRIFICE_UUID = "sacrifice"
        }

        private var sacrificeId: UUID? = null

        override fun load(data: CompoundTag) {
            sacrificeId = if (data.contains(KEY_SACRIFICE_UUID, 11)) NbtUtils.loadUUID(data[KEY_SACRIFICE_UUID])
            else null
        }

        override fun saveAdditional(data: CompoundTag) {
            sacrificeId?.let { data.put(KEY_SACRIFICE_UUID, NbtUtils.createUUID(it)) }
        }

        var sacrifice: LivingEntity?
            get() = sacrificeId?.let { (level as? ServerLevel)?.getEntity(it) as? LivingEntity }
            set(target) {
                sacrificeId = target?.uuid
                setChanged()
            }
    }
}
