package io.yukkuric.hexautomata.events

sealed class BuiltinEventMarker : EventMarker() {
    object HURT : BuiltinEventMarker()
    object TARGETED : BuiltinEventMarker()
    object SHOOT : BuiltinEventMarker()
    object PROJECTILE_HIT : BuiltinEventMarker()
}