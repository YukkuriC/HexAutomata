package io.yukkuric.hexautomata.action_patch

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation

open class PatchAction(val original: Action, val patcher: Action) : Action {
    object USE_ORIGINAL : Throwable() {
        private fun readResolve(): Any = USE_ORIGINAL
        override fun fillInStackTrace() = this
    }

    override fun operate(
        env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation
    ): OperationResult {
        try {
            return patcher.operate(env, image, continuation)
        } catch (e: Throwable) {
            if (e == USE_ORIGINAL) return original.operate(env, image, continuation)
            throw e
        }
    }
}