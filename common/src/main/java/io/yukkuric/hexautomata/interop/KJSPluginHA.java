package io.yukkuric.hexautomata.interop;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import io.yukkuric.hexautomata.action_patch.HAPatches;
import io.yukkuric.hexautomata.action_patch.PatchAction;
import io.yukkuric.hexautomata.action_patch.brainsweep.BrainsweepCallback;

public class KJSPluginHA implements KubeJSPlugin {
    public void registerBindings(BindingRegistry event) {
        if (event.type().isClient()) return;
        event.add("BrainsweepCallback", BrainsweepCallback.class);
        event.add("PatchAction", PatchAction.class);
        event.add("HAPatches", HAPatches.INSTANCE);
    }
}
