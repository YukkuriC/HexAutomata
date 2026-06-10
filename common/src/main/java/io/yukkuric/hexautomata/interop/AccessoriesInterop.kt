// 生成于 GLM-5V-Turbo
package io.yukkuric.hexautomata.interop

import io.yukkuric.hexautomata.collector.AccessoriesCollector
import io.yukkuric.hexautomata.items.collector.FocusCollector

object AccessoriesInterop : Runnable {
    override fun run() {
        // TODO: register accessories collector
        FocusCollector.register("accessory", AccessoriesCollector)
    }
}
