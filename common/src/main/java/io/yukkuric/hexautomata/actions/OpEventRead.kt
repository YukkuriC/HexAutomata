package io.yukkuric.hexautomata.actions

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.xplat.IXplatAbstractions
import io.yukkuric.hexautomata.casting.EntityEventEnv

// https://github.com/FallingColors/HexMod/blob/main/Common/src/main/java/at/petrak/hexcasting/common/casting/actions/rw/OpRead.kt
object OpEventRead : EventLimitedAction() {
    override val argc = 0
    override fun execute(args: List<Iota>, env: EntityEventEnv): List<Iota> {
        val stack = env.stack
        val datumHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack)
            ?: throw MishapBadOffhandItem.of(stack, "iota.read")
        val datum = datumHolder.readIota(env.world)
            ?: datumHolder.emptyIota()
            ?: throw MishapBadOffhandItem.of(stack, "iota.read")
        return listOf(datum)
    }
}