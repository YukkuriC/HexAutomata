package io.yukkuric.hexautomata.forge.interop

import io.yukkuric.hexautomata.forge.collector.CuriosCollector
import io.yukkuric.hexautomata.items.collector.FocusCollector

object CuriosInterop : Runnable {
    override fun run() {
        FocusCollector.register("curios", CuriosCollector)
    }
}