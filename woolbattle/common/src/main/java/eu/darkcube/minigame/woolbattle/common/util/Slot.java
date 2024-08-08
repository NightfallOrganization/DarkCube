package eu.darkcube.minigame.woolbattle.common.util;

import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.annotations.Api;

@Api
public final class Slot {
    private static final Mappings MAPPINGS = WoolBattleProvider.PROVIDER.service(Mappings.class);
    private static int id = 0;

    private static int id() {
        return id++;
    }

    public static Mappings mappings() {
        return MAPPINGS;
    }

    public interface Inventory {
        int SLOT_1 = id();
        int SLOT_2 = id();
        int SLOT_3 = id();
        int SLOT_4 = id();
        int SLOT_5 = id();
        int SLOT_6 = id();
        int SLOT_7 = id();
        int SLOT_8 = id();
        int SLOT_9 = id();
        int SLOT_10 = id();
        int SLOT_11 = id();
        int SLOT_12 = id();
        int SLOT_13 = id();
        int SLOT_14 = id();
        int SLOT_15 = id();
        int SLOT_16 = id();
        int SLOT_17 = id();
        int SLOT_18 = id();
        int SLOT_19 = id();
        int SLOT_20 = id();
        int SLOT_21 = id();
        int SLOT_22 = id();
        int SLOT_23 = id();
        int SLOT_24 = id();
        int SLOT_25 = id();
        int SLOT_26 = id();
        int SLOT_27 = id();

        int[] SLOTS = {SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6, SLOT_7, SLOT_8, SLOT_9, SLOT_10, SLOT_11, SLOT_12, SLOT_13, SLOT_14, SLOT_15, SLOT_16, SLOT_17, SLOT_18, SLOT_19, SLOT_20, SLOT_21, SLOT_22, SLOT_23, SLOT_24, SLOT_25, SLOT_26, SLOT_27};
    }

    public interface Hotbar {
        int SLOT_1 = id();
        int SLOT_2 = id();
        int SLOT_3 = id();
        int SLOT_4 = id();
        int SLOT_5 = id();
        int SLOT_6 = id();
        int SLOT_7 = id();
        int SLOT_8 = id();
        int SLOT_9 = id();

        int[] SLOTS = {SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6, SLOT_7, SLOT_8, SLOT_9};
    }

    public interface Armor {
        int HEAD = id();
        int CHEST = id();
        int PANTS = id();
        int BOOTS = id();
    }

    public interface CraftingField {
        int SLOT_1 = id();
        int SLOT_2 = id();
        int SLOT_3 = id();
        int SLOT_4 = id();
        int PRODUCT = id();
    }

    public interface Mappings {
        int platformSlot(int id);
    }
}
