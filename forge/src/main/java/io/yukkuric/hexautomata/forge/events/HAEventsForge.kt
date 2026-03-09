package io.yukkuric.hexautomata.forge.events

import io.yukkuric.hexautomata.events.IHAEvent
import net.minecraft.world.entity.LivingEntity
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.ProjectileImpactEvent
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent

object HAEventsForge {
    class Hurt(override val raw: LivingHurtEvent) : IHAEvent.ExtraDouble(), IHAForgeEvent<LivingHurtEvent> {
        override val data = raw.amount.toDouble()
        override val entity = raw.source.entity
    }

    class Targeted(override val raw: LivingChangeTargetEvent, override val data: Double) : IHAEvent.ExtraDouble(),
        IHAForgeEvent<LivingChangeTargetEvent> {
        override val entity: LivingEntity = raw.entity
    }

    class Shoot(raw: EntityJoinLevelEvent) : IHAForgeEvent.Simple<EntityJoinLevelEvent>(raw)

    class ProjectileHit(override val raw: ProjectileImpactEvent) :
        IHAEvent.CommonProjHit(raw.projectile, raw.rayTraceResult), IHAForgeEvent<ProjectileImpactEvent>
}