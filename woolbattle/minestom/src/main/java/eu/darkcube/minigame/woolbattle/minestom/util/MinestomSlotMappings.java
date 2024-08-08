package eu.darkcube.minigame.woolbattle.minestom.util;

import eu.darkcube.minigame.woolbattle.common.util.Slot;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class MinestomSlotMappings implements Slot.Mappings {
    private final int[] mappings;

    public MinestomSlotMappings() {
        var mappings = new IntArrayList();
        register(mappings, Slot.Inventory.SLOT_1, 9, 27);
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
