package io.yukkuric.hexautomata.events

abstract class EventMarker(setName: String? = null) {
    val name: String = setName ?: javaClass.simpleName

    init {
        TYPES.put(name, this)?.let {
            throw RuntimeException("duped event marker name: $name")
        }
    }

    object HURT : EventMarker()
    object TARGETED : EventMarker()
    object SHOOT : EventMarker()
    object PROJECTILE_HIT : EventMarker()

    companion object {
        private val TYPES = HashMap<String, EventMarker>()

        // ref all preset events to force-load
        val VANILLA = listOf(HURT, TARGETED, SHOOT, PROJECTILE_HIT)

        @JvmStatic
        fun all() = TYPES.values

        @JvmStatic
        fun of(name: String) = TYPES[name]
    }
}