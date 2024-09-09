/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.annotations.Api;

@Api
public final class Slot {
    private static final Collection<Integer> IDS = Collections.synchronizedList(new ArrayList<>());

    private static int id(int id) {
        if (!IDS.add(id)) throw new IllegalStateException("Duplicate slot id: " + id);
        return id;
    }

    public static Mappings mappings() {
        return SlotImpl.MAPPINGS;
    }

    public interface Inventory {
        int SLOT_1 = id(0);
        int SLOT_2 = id(1);
        int SLOT_3 = id(2);
        int SLOT_4 = id(3);
        int SLOT_5 = id(4);
        int SLOT_6 = id(5);
        int SLOT_7 = id(6);
        int SLOT_8 = id(7);
        int SLOT_9 = id(8);
        int SLOT_10 = id(9);
        int SLOT_11 = id(10);
        int SLOT_12 = id(11);
        int SLOT_13 = id(12);
        int SLOT_14 = id(13);
        int SLOT_15 = id(14);
        int SLOT_16 = id(15);
        int SLOT_17 = id(16);
        int SLOT_18 = id(17);
        int SLOT_19 = id(18);
        int SLOT_20 = id(19);
        int SLOT_21 = id(20);
        int SLOT_22 = id(21);
        int SLOT_23 = id(22);
        int SLOT_24 = id(23);
        int SLOT_25 = id(24);
        int SLOT_26 = id(25);
        int SLOT_27 = id(26);

        int[] SLOTS = {SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6, SLOT_7, SLOT_8, SLOT_9, SLOT_10, SLOT_11, SLOT_12, SLOT_13, SLOT_14, SLOT_15, SLOT_16, SLOT_17, SLOT_18, SLOT_19, SLOT_20, SLOT_21, SLOT_22, SLOT_23, SLOT_24, SLOT_25, SLOT_26, SLOT_27};
    }

    public interface Hotbar {
        int SLOT_1 = id(27);
        int SLOT_2 = id(28);
        int SLOT_3 = id(29);
        int SLOT_4 = id(30);
        int SLOT_5 = id(31);
        int SLOT_6 = id(32);
        int SLOT_7 = id(33);
        int SLOT_8 = id(34);
        int SLOT_9 = id(35);
        int OFFHAND = id(36);

        int[] SLOTS = {SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6, SLOT_7, SLOT_8, SLOT_9};
    }

    public interface Armor {
        int HEAD = id(37);
        int CHEST = id(38);
        int PANTS = id(39);
        int BOOTS = id(40);
    }

    public interface CraftingField {
        int SLOT_1 = id(41);
        int SLOT_2 = id(42);
        int SLOT_3 = id(43);
        int SLOT_4 = id(44);
        int PRODUCT = id(45);
    }

    public interface Cursor {
        int SLOT = id(46);
    }

    public interface Mappings {
        int platformSlot(int id);
    }

    /**
     * This is in a separate class because of class loader order problems
     */
    private static class SlotImpl {
        private static final Mappings MAPPINGS = WoolBattleProvider.PROVIDER.service(Mappings.class);
    }
}
