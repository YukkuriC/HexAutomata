package io.yukkuric.hexautomata.events

abstract class EventMarker(setName: String? = null) {
    val name: String = setName ?: javaClass.simpleName

    init {
        TYPES.put(name, this)?.let {
            throw RuntimeException("duped event marker name: $name")
        }
    }

    companion object {
        private val TYPES = LinkedHashMap<String, EventMarker>()

        // ref all preset events to force-load
        val VANILLA = BuiltinEventMarker::class.sealedSubclasses.map { cls -> cls.objectInstance!! }

        @JvmStatic
        fun all() = TYPES.values

        @JvmStatic
        fun of(name: String) = TYPES[name]
    }
}