package io.yukkuric.hexautomata.multiblock

import com.google.common.base.Supplier
import io.yukkuric.hexautomata.helpers.CustomRegisterObject
import io.yukkuric.hexautomata.multiblock.rituals.RitualCollector
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Rotation
import vazkii.patchouli.api.IMultiblock
import vazkii.patchouli.api.PatchouliAPI

object HARituals : CustomRegisterObject<Supplier<IMultiblock>>() {
    fun load() {
        val api = PatchouliAPI.get()
        for (pair in entries) {
            api.registerMultiblock(pair.key, pair.value.get())
        }
    }

    object PostRitualEffects : CustomRegisterObject<(ServerLevel, BlockPos, IMultiblock, Rotation) -> Any?>()

    init {
        RitualCollector.load()
    }
}
