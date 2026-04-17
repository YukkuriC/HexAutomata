package io.yukkuric.hexautomata.entity

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Slime
import net.minecraft.world.level.Level

class MediaSlime(entityType: EntityType<out MediaSlime>, level: Level) : Slime(entityType, level) {
    override fun getHealth(): Float {
        return maxHealth
    }

    override fun setHealth(f: Float) {
    }

    override fun die(damageSource: DamageSource) {
        if (health > 0) return
        super.die(damageSource)
    }

    override fun isDeadOrDying(): Boolean {
        if (health > 0) return false
        return super.isDeadOrDying()
    }

    override fun tickDeath() {
        if (health > 0) return
        super.tickDeath()
    }

    override fun removeWhenFarAway(d: Double) = false
    override fun remove(removalReason: RemovalReason) {}
}