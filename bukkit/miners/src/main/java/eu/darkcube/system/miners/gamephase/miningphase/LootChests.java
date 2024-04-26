/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.miningphase;

import eu.darkcube.system.miners.items.Item;
import eu.darkcube.system.util.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootChests {

    public static final Random RANDOM = new Random();

    public static void createLootChest(Location location) {
        location.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) location.getBlock().getState();
        chest.getBlockInventory().setContents(generateChestContent());
        chest.update(true);
    }

    public static ItemStack[] generateChestContent() {
        ArrayList<ItemStack> list = new ArrayList<>();
        LootItem.LOOT_TABLE.forEach(i -> {
            ItemStack is = i.get();
            if (is != null)
                list.add(is);
        });
        fillList(list);
        return fillArray(list);
    }

    private static ItemStack[] fillArray(ArrayList<ItemStack> src) {
        ItemStack[] result = new ItemStack[27];
        ArrayList<ItemStack> srcClone = new ArrayList<>(src);
        for (int i = 0; i < 27; i++) {
            if (!srcClone.isEmpty()) {
                int rand = RANDOM.nextInt(srcClone.size() - 1);
                result[i] = srcClone.get(rand);
                srcClone.remove(rand);
            } else
                result[i] = null;
        }
        return result;
    }

    private static void fillList(ArrayList<ItemStack> src) {
        while (src.size() < 27)
            src.add(new ItemStack(Material.AIR));
    }

    private static class LootItem {

        public static final List<LootItem> LOOT_TABLE = Arrays.asList(new LootItem[]{
                new LootItem(Item.FLINT, 1, 5, 0.2),
                new LootItem(Item.INGOT_IRON, 1, 4, 0.15)
        });

        private final Item item;
        private final int min;
        private final int max;
        private final double dropChance;

        public LootItem(Item item, int min, int max, double dropChance) {
            this.item = item;
            this.min = min;
            this.max = max;
            this.dropChance = dropChance;
        }

        public ItemStack get() {
            if (RANDOM.nextFloat() > dropChance)
                return null;
            return item.getItem(Language.DEFAULT, RANDOM.nextInt(max + min) - min);
        }

    }

}
