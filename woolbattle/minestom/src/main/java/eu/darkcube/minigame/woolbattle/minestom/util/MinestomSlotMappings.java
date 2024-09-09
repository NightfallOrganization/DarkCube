/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.util;

import eu.darkcube.minigame.woolbattle.common.util.Slot;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class MinestomSlotMappings implements Slot.Mappings {
    private final int[] mappings;

    public MinestomSlotMappings() {
        var mappings = new IntArrayList();
        register(mappings, Slot.Hotbar.SLOT_1, 0, 9);
        register(mappings, Slot.Inventory.SLOT_1, 9, 27);
        register(mappings, Slot.Armor.HEAD, 41);
        register(mappings, Slot.Armor.CHEST, 42);
        register(mappings, Slot.Armor.PANTS, 43);
        register(mappings, Slot.Armor.BOOTS, 44);
        register(mappings, Slot.Hotbar.OFFHAND, 45);
        register(mappings, Slot.CraftingField.PRODUCT, 36);
        register(mappings, Slot.CraftingField.SLOT_1, 37, 4);
        this.mappings = mappings.toIntArray();
    }

    private void register(IntList m, int woolbattle, int platform) {
        if (m.size() <= woolbattle) {
            m.size(woolbattle + 1);
        }
        m.set(woolbattle, platform);
    }

    private void register(IntList m, int woolbattleStart, int platformStart, int length) {
        for (var i = 0; i < length; i++) {
            register(m, woolbattleStart + i, platformStart + i);
        }
    }

    @Override
    public int platformSlot(int id) {
        return mappings[id];
    }
}
