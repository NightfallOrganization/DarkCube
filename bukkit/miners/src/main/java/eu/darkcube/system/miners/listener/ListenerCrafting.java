/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.listener;

import eu.darkcube.system.miners.items.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.HashMap;
import java.util.Map;

public class ListenerCrafting implements Listener {

    private final Map<Material, Item> CRAFTING_ITEMS;

    public ListenerCrafting() {
        CRAFTING_ITEMS = new HashMap<>();
        CRAFTING_ITEMS.put(Material.IRON_PICKAXE, Item.PICKAXE_IRON);
        CRAFTING_ITEMS.put(Material.DIAMOND_PICKAXE, Item.PICKAXE_DIAMOND);
        CRAFTING_ITEMS.put(Material.GOLD_PICKAXE, Item.PICKAXE_GOLD);
        CRAFTING_ITEMS.put(Material.SHEARS, Item.SHEARS);
        CRAFTING_ITEMS.put(Material.FLINT_AND_STEEL, Item.FLINT_AND_STEEL);
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
//        if (!e.getSlotType().equals(InventoryType.SlotType.RESULT))
//            return;
//        if (!(e.getClickedInventory().getType().equals(InventoryType.CRAFTING) || e.getClickedInventory().getType().equals(InventoryType.WORKBENCH)))
//            return;
//        if (e.getClickedInventory().contains(Material.COBBLESTONE)) {
//            e.setCancelled(true);
//            return;
//        }
        if (!CRAFTING_ITEMS.containsKey(e.getCurrentItem().getType())) {
            e.setCancelled(true);
            return;
        }
        e.setCurrentItem(CRAFTING_ITEMS.get(e.getCurrentItem().getType()).getItem((Player) e.getWhoClicked(), e.getCurrentItem().getAmount()));
    }

}
