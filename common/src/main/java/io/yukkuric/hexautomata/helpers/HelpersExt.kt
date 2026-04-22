package io.yukkuric.hexautomata.helpers

import io.yukkuric.hexautomata.blocks.ISacrificeRecorder
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

private val CACHED_ADVANCEMENTS = HashMap<ResourceLocation, AdvancementHolder?>()
fun ServerPlayer.grantAdvancement(id: ResourceLocation, vararg criteria: String = arrayOf("root")) {
    val adv = CACHED_ADVANCEMENTS.computeIfAbsent(id, server.advancements::get) ?: return
    for (c in criteria) advancements.award(adv, c)
}

fun ServerPlayer.hasAdvancement(id: ResourceLocation): Boolean {
    val adv = CACHED_ADVANCEMENTS.computeIfAbsent(id, server.advancements::get) ?: return false
    val progress = advancements.getOrStartProgress(adv)
    return progress.isDone
}

fun ServerLevel.tryRecordBrainsweepSacrifice(pos: BlockPos, sacrifice: Entity) {
    val be = getBlockEntity(pos)
    if (be is ISacrificeRecorder) be.sacrifice = sacrifice
}