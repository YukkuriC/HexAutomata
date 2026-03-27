package io.yukkuric.hexautomata.forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import static io.yukkuric.hexautomata.HAConfig.*;

public class HAConfigForge implements API {
    public static HAConfigForge INSTANCE;

    public boolean EnablesFocusInHands() {
        return cfg_EnablesFocusInHands.get();
    }
    public boolean EnablesFocusInsideInventory() {
        return cfg_EnablesFocusInsideInventory.get();
    }
    public boolean EnablesFocusInsideEnderChest() {
        return cfg_EnablesFocusInsideEnderChest.get();
    }
    public boolean EnablesFocusInsideAccessories() {
        return cfg_EnablesFocusInsideAccessories.get();
    }
    public boolean FirstFocusOnly() {
        return cfg_FirstFocusOnly.get();
    }
    public int MaxRecursiveEventsPerTick() {
        return cfg_MaxRecursiveEventsPerTick.get();
    }
    public int EventTriggerTax() {
        return cfg_EventTriggerTax.get();
    }

    public ForgeConfigSpec.BooleanValue
            cfg_EnablesFocusInHands,
            cfg_EnablesFocusInsideInventory,
            cfg_EnablesFocusInsideEnderChest,
            cfg_EnablesFocusInsideAccessories,
            cfg_FirstFocusOnly;
    public ForgeConfigSpec.IntValue
            cfg_MaxRecursiveEventsPerTick,
            cfg_EventTriggerTax;

    public HAConfigForge(ForgeConfigSpec.Builder builder) {
        builder.push("Scope");
        cfg_EnablesFocusInHands = builder.comment(desc_EnablesFocusInHands).define("EnablesFocusInHands", true);
        cfg_EnablesFocusInsideInventory = builder.comment(desc_EnablesFocusInsideInventory).define("EnablesFocusInsideInventory", false);
        cfg_EnablesFocusInsideEnderChest = builder.comment(desc_EnablesFocusInsideEnderChest).define("EnablesFocusInsideEnderChest", false);
        cfg_EnablesFocusInsideAccessories = builder.comment(desc_EnablesFocusInsideAccessories).define("EnablesFocusInsideAccessories", true);
        cfg_FirstFocusOnly = builder.comment(desc_FirstFocusOnly).define("FirstFocusOnly", true);
        builder.pop();

        builder.push("Execute");
        cfg_MaxRecursiveEventsPerTick = builder.comment(desc_MaxRecursiveEventsPerTick).defineInRange("MaxRecursiveEventsPerTick", 10, 0, 114514);
        cfg_EventTriggerTax = builder.comment(desc_EventTriggerTax).defineInRange("EventTriggerTax", 0, 0, Integer.MAX_VALUE);
        builder.pop();

        INSTANCE = this;
    }

    private static final Pair<HAConfigForge, ForgeConfigSpec> CFG_REGISTRY;

    static {
        CFG_REGISTRY = new ForgeConfigSpec.Builder().configure(HAConfigForge::new);
    }

    public static void register(ModLoadingContext ctx) {
        bindConfigImp(CFG_REGISTRY.getKey());
        ctx.registerConfig(ModConfig.Type.COMMON, CFG_REGISTRY.getValue());
    }
}
