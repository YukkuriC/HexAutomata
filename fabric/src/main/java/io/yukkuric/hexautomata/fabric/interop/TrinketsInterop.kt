package io.yukkuric.hexautomata.fabric.interop

import io.yukkuric.hexautomata.fabric.collector.TrinketsCollector
import io.yukkuric.hexautomata.items.collector.FocusCollector

object TrinketsInterop : Runnable {
    override fun run() {
        FocusCollector.register("accessory", TrinketsCollector)
    }
}