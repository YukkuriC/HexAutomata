package io.yukkuric.hexautomata.forge.events

import io.yukkuric.hexautomata.events.IHAEvent
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent

object HAEventsForge {
    open class Hurt(override val raw: LivingDamageEvent.Pre) : IHAEvent.ExtraDouble(),
        IHAForgeEvent<LivingDamageEvent.Pre> {
        override val data = raw.newDamage.toDouble()
        override val entity = raw.source.entity
    }

    class PlayerAttack(raw: LivingDamageEvent.Pre) : Hurt(raw) {
        override val entity = raw.entity
    }

    class Kill(raw: LivingDeathEvent) : IHAForgeEvent.Simple<LivingDeathEvent>(raw)

    class ProjectileHit(override val raw: ProjectileImpactEvent) :
        IHAEvent.CommonProjHit(raw.projectile, raw.rayTraceResult), IHAForgeEvent<ProjectileImpactEvent>
}