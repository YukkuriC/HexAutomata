package io.yukkuric.hexautomata.helpers

open class SinglePutMap<K, T> {
    val MAP = LinkedHashMap<K, T>()
    operator fun get(key: K) = MAP[key]
    operator fun set(key: K, obj: T): T {
        val old = MAP.put(key, obj)
        if (old != null) throw IllegalArgumentException("duped id $key in type $javaClass")
        return obj
    }
}