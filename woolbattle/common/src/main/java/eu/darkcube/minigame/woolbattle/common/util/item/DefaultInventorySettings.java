/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class DefaultInventorySettings {
    private static final String MASK5x9 = """
            lllldllll
            dddlllddd
            ldddldddl
            dldldldld
            ldldldldl
            """;
    private static final String MASK_PAGED_5x9 = """
            lllldllll
            pddlllddn
            pdddldddn
            pldldldln
            ldldldldl
            """;
    private static final String MASK_PAGE_SLOTS_5x9 = """
            .........
            .#######.
            .#######.
            .#######.
            .........
            """;
    public static final int[] SLOTS_PAGED_PREV_5x9 = slots(MASK_PAGED_5x9, 'p');
    public static final int[] SLOTS_PAGED_NEXT_5x9 = slots(MASK_PAGED_5x9, 'n');
    public static final int[] PAGE_SLOTS_5x9 = slots(MASK_PAGE_SLOTS_5x9, '#');

    @SuppressWarnings("SameParameterValue")
    private static int[] slots(@NotNull String mask, char character) {
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

    public static @NotNull ItemTemplate create() {
        return ItemTemplate.create();
    }

    public static void setup(@NotNull ItemTemplate template) {
        template.setItem(Items.GRAY_GLASS_PANE, slots(MASK5x9, 'l'));
        template.setItem(Items.BLACK_GLASS_PANE, slots(MASK5x9, 'd'));
    }

    public static void setupPaged(@NotNull ItemTemplate template) {
        template.setItem(Items.GRAY_GLASS_PANE, slots(MASK_PAGED_5x9, 'l'));
        template.setItem(Items.BLACK_GLASS_PANE, slots(MASK_PAGED_5x9, 'd'));
        template.setItem(Items.PREV_PAGE_UNUSABLE, SLOTS_PAGED_PREV_5x9);
        template.setItem(Items.NEXT_PAGE_UNUSABLE, SLOTS_PAGED_NEXT_5x9);
    }
}
