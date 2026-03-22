package io.yukkuric.hexautomata.multiblock.rituals

import com.google.common.base.Supplier
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.multiblock.HARituals
import io.yukkuric.hexautomata.multiblock.HARituals.PostRitualEffects
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Rotation
import vazkii.patchouli.api.IMultiblock

sealed class RitualCollector {
    abstract val ritual: Supplier<IMultiblock>
    open val postEffect: ((ServerLevel, BlockPos, IMultiblock, Rotation) -> Any?)? = null
    open val idStr get():String = TODO("id")
    open val idLoc get() = HexAutomata.modLoc(idStr)

    fun registerSelf() {
        HARituals[idLoc] = ritual
        postEffect?.let { PostRitualEffects[idLoc] = it }
    }

    companion object {
        fun load() {
            RitualCollector::class.sealedSubclasses.mapNotNull { it.objectInstance }.forEach {
                HexAutomata.LOGGER.info("loading ritual group: $it")
                it.registerSelf()
            }
        }
    }
}