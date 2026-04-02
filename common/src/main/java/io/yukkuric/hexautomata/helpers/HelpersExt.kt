package io.yukkuric.hexautomata.helpers

import io.yukkuric.hexautomata.blocks.ISacrificeRecorder
import net.minecraft.advancements.Advancement
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

private val CACHED_ADVANCEMENTS = HashMap<ResourceLocation, Advancement?>()
fun ServerPlayer.grantAdvancement(id: ResourceLocation, vararg criteria: String = arrayOf("root")) {
    val adv = CACHED_ADVANCEMENTS.computeIfAbsent(id, server.advancements::getAdvancement) ?: return
    for (c in criteria) advancements.award(adv, c)
}

fun ServerLevel.tryRecordBrainsweepSacrifice(pos: BlockPos, sacrifice: Entity) {
    val be = getBlockEntity(pos)
    if (be is ISacrificeRecorder) be.sacrifice = sacrifice
}