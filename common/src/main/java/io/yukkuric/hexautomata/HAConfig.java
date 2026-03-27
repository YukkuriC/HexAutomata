package io.yukkuric.hexautomata;

public class HAConfig {
    static API imp;

    public static boolean loaded() {
        return imp != null;
    }
    public static void bindConfigImp(API api) {
        imp = api;
    }

    public static boolean EnablesFocusInHands() {
        return imp.EnablesFocusInHands();
    }
    public static boolean EnablesFocusInsideInventory() {
        return imp.EnablesFocusInsideInventory();
    }
    public static boolean EnablesFocusInsideEnderChest() {
        return imp.EnablesFocusInsideEnderChest();
    }
    public static boolean EnablesFocusInsideAccessories() {
        return imp.EnablesFocusInsideAccessories();
    }
    public static boolean FirstFocusOnly() {
        return imp.FirstFocusOnly();
    }
    public static int MaxRecursiveEventsPerTick() {
        return imp.MaxRecursiveEventsPerTick();
    }
    public interface API {
        String desc_EnablesFocusInHands = "Reactive Focus(es) inside player's hands take effect; ignored if `Inventory` set to true";
        String desc_EnablesFocusInsideInventory = "Reactive Focus(es) inside player's inventory (including main/off hand) take effect";
        String desc_EnablesFocusInsideEnderChest = "Reactive Focus(es) inside player's ender chest take effect";
        String desc_EnablesFocusInsideAccessories = "Reactive Focus(es) inside player's curios/trinkets slots take effect";
        String desc_FirstFocusOnly = "Only the first Reactive Focus of a type met take effect; if set to false, all Focuses of certain type are triggered for each event (may cause performance impact)";
        String desc_MaxRecursiveEventsPerTick = "Recursive triggered events in a tick deeper than this value will be rejected (e.g. teleport self inside `Teleported` focus)";

        boolean EnablesFocusInHands();
        boolean EnablesFocusInsideInventory();
        boolean EnablesFocusInsideEnderChest();
        boolean EnablesFocusInsideAccessories();
        boolean FirstFocusOnly();
        int MaxRecursiveEventsPerTick();
    }
}
