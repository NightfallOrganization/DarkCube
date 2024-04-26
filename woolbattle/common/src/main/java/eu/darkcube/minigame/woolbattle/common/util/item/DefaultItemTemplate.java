/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import eu.darkcube.system.server.inventory.item.ItemTemplate;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class DefaultItemTemplate {
    private static final String MASK5x9 = """
            lllldllll
            dddlllddd
            ldddldddl
            dldldldld
            ldldldldl
            """;

    @SuppressWarnings("SameParameterValue")
    private static int[] slots(String mask, char character) {
        var simplified = mask.replace("\n", "");
        var slots = new IntArrayList();
        for (var slot = 0; slot < simplified.length(); slot++) {
            var c = simplified.charAt(slot);
            if (c == character) {
                slots.add(slot);
            }
        }
        return slots.toIntArray();
    }

    public static ItemTemplate create() {
        return ItemTemplate.create();
    }

    public static void setup(ItemTemplate template) {
        template.setItem(Items.GRAY_GLASS_PANE, slots(MASK5x9, 'l'));
        template.setItem(Items.BLACK_GLASS_PANE, slots(MASK5x9, 'd'));
    }
}
