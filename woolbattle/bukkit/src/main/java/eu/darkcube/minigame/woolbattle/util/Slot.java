/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

@SuppressWarnings("unused")
public interface Slot {

    interface Inventory {
        int SLOT_1 = 9;
        int SLOT_2 = 10;
        int SLOT_3 = 11;
        int SLOT_4 = 12;
        int SLOT_5 = 13;
        int SLOT_6 = 14;
        int SLOT_7 = 15;
        int SLOT_8 = 16;
        int SLOT_9 = 17;
        int SLOT_10 = 18;
        int SLOT_11 = 19;
        int SLOT_12 = 20;
        int SLOT_13 = 21;
        int SLOT_14 = 22;
        int SLOT_15 = 23;
        int SLOT_16 = 24;
        int SLOT_17 = 25;
        int SLOT_18 = 26;
        int SLOT_19 = 27;
        int SLOT_20 = 28;
        int SLOT_21 = 29;
        int SLOT_22 = 30;
        int SLOT_23 = 31;
        int SLOT_24 = 32;
        int SLOT_25 = 33;
        int SLOT_26 = 34;
        int SLOT_27 = 35;

        int[] SLOTS = new int[]{SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6, SLOT_7, SLOT_8, SLOT_9, SLOT_10, SLOT_11, SLOT_12, SLOT_13, SLOT_14, SLOT_15, SLOT_16, SLOT_17, SLOT_18, SLOT_19, SLOT_20, SLOT_21, SLOT_22, SLOT_23, SLOT_24, SLOT_25, SLOT_26, SLOT_27};
    }

    interface Hotbar {
        int SLOT_1 = 36;
        int SLOT_2 = 37;
        int SLOT_3 = 38;
        int SLOT_4 = 39;
        int SLOT_5 = 40;
        int SLOT_6 = 41;
        int SLOT_7 = 42;
        int SLOT_8 = 43;
        int SLOT_9 = 44;

        int[] SLOTS = new int[]{SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6, SLOT_7, SLOT_8, SLOT_9};
    }

    interface Armor {
        int HEAD = 5;
        int CHEST = 6;
        int PANTS = 7;
        int BOOTS = 8;
    }

    interface CraftingField {
        int SLOT_1 = 1;
        int SLOT_2 = 2;
        int SLOT_3 = 3;
        int SLOT_4 = 4;
        int PRODUCT = 0;
    }
}
