package io.yukkuric.hexautomata.fabric.events

import io.yukkuric.hexautomata.events.IHAEvent
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.phys.HitResult

object HAEventsFabric {
    class Hurt(override val entity: Entity?, override val data: Double) : IHAEvent.ExtraDouble()
    class Targeted(override val entity: Entity?, override val data: Double) : IHAEvent.ExtraDouble()
    class Shoot(override val entity: Entity?) : IHAEvent
    class ProjectileHit(entity: Projectile, hit: HitResult) : IHAEvent.CommonProjHit(entity, hit)
}