package io.yukkuric.hexautomata.tag

import io.yukkuric.hexautomata.HexAutomata
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType

abstract class HATags<T>(val registry: ResourceKey<out Registry<T>>) {
    fun create(name: String): TagKey<T> {
        return TagKey.create(registry, HexAutomata.modLoc(name))
    }

    object Entity : HATags<EntityType<*>>(Registries.ENTITY_TYPE) {
        val IGNORE_TARGETING = create("ignore_targeting")
        val IGNORE_HURT = create("ignore_hurt")
    }

    object Damage : HATags<DamageType>(Registries.DAMAGE_TYPE) {
        val IGNORE_HURT = create("ignore_hurt")
    }
}