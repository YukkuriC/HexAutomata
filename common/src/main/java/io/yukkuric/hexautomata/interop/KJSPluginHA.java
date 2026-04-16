package io.yukkuric.hexautomata.interop;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.yukkuric.hexautomata.action_patch.HAPatches;
import io.yukkuric.hexautomata.action_patch.PatchAction;
import io.yukkuric.hexautomata.action_patch.brainsweep.BrainsweepCallback;

public class KJSPluginHA extends KubeJSPlugin {
    public void registerBindings(BindingsEvent event) {
        if (event.manager.scriptType == ScriptType.CLIENT) return;
        event.add("BrainsweepCallback", BrainsweepCallback.class);
        event.add("PatchAction", PatchAction.class);
        event.add("HAPatches", HAPatches.INSTANCE);
    }
}
