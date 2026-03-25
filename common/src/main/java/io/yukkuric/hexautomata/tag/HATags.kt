package io.yukkuric.hexautomata.tag

import io.yukkuric.hexautomata.HexAutomata
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType

object HATags {
    object Entity {
        val IGNORE_TARGETING = create("ignore_targeting")
        fun create(name: String): TagKey<EntityType<*>> {
            return TagKey.create(Registries.ENTITY_TYPE, HexAutomata.modLoc(name))
        }
    }
}