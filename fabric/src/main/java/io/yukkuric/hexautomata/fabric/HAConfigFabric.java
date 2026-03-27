package io.yukkuric.hexautomata.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import static io.yukkuric.hexautomata.HAConfig.*;

@Config(name = "HexAutomata")
public class HAConfigFabric implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    private final Common common = new Common();

    public static void setup() {
        AutoConfig.register(HAConfigFabric.class, JanksonConfigSerializer::new);
        var instance = AutoConfig.getConfigHolder(HAConfigFabric.class).getConfig();
        bindConfigImp(instance.common);
    }

    public static class Common implements API, ConfigData {
        @Comment("<Scope> " + desc_EnablesFocusInHands)
        private boolean EnablesFocusInHands = true;
        @Comment("<Scope> " + desc_EnablesFocusInsideInventory)
        private boolean EnablesFocusInsideInventory = false;
        @Comment("<Scope> " + desc_EnablesFocusInsideEnderChest)
        private boolean EnablesFocusInsideEnderChest = false;
        @Comment("<Scope> " + desc_EnablesFocusInsideAccessories)
        private boolean EnablesFocusInsideAccessories = true;
        @Comment("<Scope> " + desc_FirstFocusOnly)
        private boolean FirstFocusOnly = true;
        @Comment("<Execute> " + desc_MaxRecursiveEventsPerTick)
        private int MaxRecursiveEventsPerTick = 10;
        @Comment("<Execute> " + desc_EventTriggerTax)
        private int EventTriggerTax = 0;

        public boolean EnablesFocusInHands() {
            return EnablesFocusInHands;
        }
        public boolean EnablesFocusInsideInventory() {
            return EnablesFocusInsideInventory;
        }
        public boolean EnablesFocusInsideEnderChest() {
            return EnablesFocusInsideEnderChest;
        }
        public boolean EnablesFocusInsideAccessories() {
            return EnablesFocusInsideAccessories;
        }
        public boolean FirstFocusOnly() {
            return FirstFocusOnly;
        }
        public int MaxRecursiveEventsPerTick() {
            return MaxRecursiveEventsPerTick;
        }
        public int EventTriggerTax() {
            return EventTriggerTax;
        }
    }
}
