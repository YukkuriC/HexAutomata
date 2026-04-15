package io.yukkuric.hexautomata.helpers

open class NoTraced : Throwable() {
    override fun fillInStackTrace() = this
}