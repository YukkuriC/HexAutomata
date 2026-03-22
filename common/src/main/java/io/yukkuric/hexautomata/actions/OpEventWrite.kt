package io.yukkuric.hexautomata.actions

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import io.yukkuric.hexautomata.casting.EntityEventEnv
import net.minecraft.server.level.ServerPlayer

// https://github.com/FallingColors/HexMod/blob/main/Common/src/main/java/at/petrak/hexcasting/common/casting/actions/rw/OpWrite.kt
object OpEventWrite : EventLimitedAction() {
    override val argc = 1
    override fun execute(args: List<Iota>, env: EntityEventEnv): List<Iota> {
        val datum = args[0]
        val stack = env.stack
        val datumHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack)
            ?: throw MishapBadOffhandItem.of(stack, "iota.write")
        if (!datumHolder.writeIota(datum, true))
            throw MishapBadOffhandItem.of(stack, "iota.readonly", datum.display())

        val trueName = MishapOthersName.getTrueNameFromDatum(datum, env.castingEntity as? ServerPlayer)
        if (trueName != null)
            throw MishapOthersName(trueName)

        datumHolder.writeIota(datum, false)
        return listOf()
    }
}