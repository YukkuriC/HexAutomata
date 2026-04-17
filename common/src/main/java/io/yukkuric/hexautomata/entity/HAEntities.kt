package io.yukkuric.hexautomata.entity

import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.helpers.CustomRegisterObject
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object HAEntities : CustomRegisterObject<EntityType<*>>() {
    val THE_SLIME = create(
        "media_slime",
        EntityType.Builder.of(::MediaSlime, MobCategory.MONSTER).sized(2f, 2f).clientTrackingRange(10)
    )

    private fun <T : Entity> create(
        name: String,
        builder: EntityType.Builder<T>,
    ): EntityType<T> {
        val key = HexAutomata.modLoc(name)
        val type = builder.build(key.toString())
        this[key] = type
        return type
    }
}