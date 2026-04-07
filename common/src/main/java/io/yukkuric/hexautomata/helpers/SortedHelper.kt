package io.yukkuric.hexautomata.helpers

class SortedHelper<T>(val dataGetter: () -> Collection<T>, val keyGetter: (T) -> Int) {
    private var sorted = false
    private lateinit var _sortedList: List<T>

    fun get(): List<T> {
        if (!sorted) {
            _sortedList = dataGetter().sortedBy(keyGetter)
            sorted = true
        }
        return _sortedList
    }

    fun setChanged() {
        sorted = false
    }
}