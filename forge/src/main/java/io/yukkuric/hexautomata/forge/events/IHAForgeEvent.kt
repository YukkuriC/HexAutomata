package io.yukkuric.hexautomata.forge.events

import io.yukkuric.hexautomata.events.IHAEvent
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.eventbus.api.Event

interface IHAForgeEvent<T : Event> {
    val raw: T

    open class Simple<T : EntityEvent>(override val raw: T) : IHAForgeEvent<T>, IHAEvent {
        override val entity = raw.entity
    }
}