package io.yukkuric.hexautomata

import net.minecraft.advancements.Advancement
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

private val CACHED_ADVANCEMENTS = HashMap<ResourceLocation, Advancement?>()
fun ServerPlayer.grantAdvancement(id: ResourceLocation, vararg criteria: String = arrayOf("root")) {
    val adv = CACHED_ADVANCEMENTS.computeIfAbsent(id, server.advancements::getAdvancement) ?: return
    for (c in criteria) advancements.award(adv, c)
}