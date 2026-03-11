package io.yukkuric.hexautomata.events

import at.petrak.hexcasting.api.casting.iota.*
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

interface IHAEvent {
    val entity: Entity?
    fun extra(): Iota? = null
    fun initStack() = listOfNotNull(entity?.let { EntityIota(it) } ?: NullIota(), extra())
    fun extraAmbitCenter() = entity?.position()

    // common event structures
    open class Simple(override val entity: Entity?) : IHAEvent

    abstract class ExtraDouble : IHAEvent {
        abstract val data: Double
        override fun extra() = DoubleIota(data)
    }

    abstract class CommonProjHit(override val entity: Projectile, val hit: HitResult) : IHAEvent {
        override fun extra() = when (hit) {
            is BlockHitResult -> Vec3Iota(hit.blockPos.center)
            is EntityHitResult -> EntityIota(hit.entity)
            else -> NullIota()
        }

        override fun extraAmbitCenter(): Vec3 = hit.location

        override fun initStack() = listOf(
            EntityIota(entity),
            Vec3Iota(extraAmbitCenter()),
            extra(),
        )
    }
}