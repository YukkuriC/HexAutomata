package io.yukkuric.hexautomata.forge.events

import io.yukkuric.hexautomata.events.IHAEvent
import net.neoforged.bus.api.Event

interface IHAForgeEvent<T : Event> {
    val raw: T

    open class Simple<T : net.neoforged.neoforge.event.entity.EntityEvent>(override val raw: T) : IHAForgeEvent<T>, IHAEvent {
        override val entity = raw.entity
    }
}