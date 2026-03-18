package io.yukkuric.hexautomata;

public class HAConfig {
    static API imp;

    public static boolean loaded() {
        return imp != null;
    }
    public static void bindConfigImp(API api) {
        imp = api;
    }

    public static boolean EnablesFocusInsideInventory() {
        return imp.EnablesFocusInsideInventory();
    }
    public static boolean EnablesFocusInsideAccessories() {
        return imp.EnablesFocusInsideAccessories();
    }
    public static boolean FirstFocusOnly() {
        return imp.FirstFocusOnly();
    }
    public interface API {
        String desc_EnablesFocusInsideInventory = "Reactive Focus(es) inside player's inventory (including main/off hand) take effect";
        String desc_EnablesFocusInsideAccessories = "Reactive Focus(es) inside player's curios/trinkets slots take effect";
        String desc_FirstFocusOnly = "Only the first Reactive Focus of a type met take effect; if set to false, all Focuses of certain type are triggered for each event (may cause performance impact)";

        boolean EnablesFocusInsideInventory();
        boolean EnablesFocusInsideAccessories();
        boolean FirstFocusOnly();
    }
}
