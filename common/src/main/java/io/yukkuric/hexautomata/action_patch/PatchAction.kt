package io.yukkuric.hexautomata.action_patch

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import dev.latvian.mods.rhino.JavaScriptException
import dev.latvian.mods.rhino.NativeJavaObject
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.helpers.NoTraced

open class PatchAction(val original: Action, val patcher: Action) : Action {
    companion object {
        @JvmStatic
        val USE_ORIGINAL = NoTraced()
        @JvmStatic
        val STOP_ALL = NoTraced()
    }

    override fun operate(
        env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation
    ): OperationResult {
        try {
            return patcher.operate(env, image, continuation)
        } catch (e: Throwable) {
            var e = e.let unwrapper@{
                if (HexAutomata.API.modLoaded("kubejs")) {
                    (((e as? JavaScriptException)?.value as? NativeJavaObject)?.unwrap() as? Throwable)
                        ?.let { return@unwrapper it }
                }
                return@unwrapper e
            }
            when (e) {
                USE_ORIGINAL -> return original.operate(env, image, continuation)
                STOP_ALL -> return OperationResult(
                    image.withUsedOp(), listOf(), SpellContinuation.Done,
                    HexEvalSounds.NOTHING,
                )
            }
            throw e
        }
    }
}