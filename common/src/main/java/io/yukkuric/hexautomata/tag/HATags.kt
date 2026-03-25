package io.yukkuric.hexautomata.tag

import io.yukkuric.hexautomata.HexAutomata
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType

object HATags {
    object Entity {
        val IGNORE_TARGETING = create("ignore_targeting")
        val IGNORE_HURT = create("ignore_hurt")
        fun create(name: String): TagKey<EntityType<*>> {
            return TagKey.create(Registries.ENTITY_TYPE, HexAutomata.modLoc(name))
        }
    }

    object Damage {
        val IGNORE_HURT = create("ignore_hurt")
        private fun create(name: String): TagKey<DamageType> {
            return TagKey.create(Registries.DAMAGE_TYPE, HexAutomata.modLoc(name))
        }
    }
}