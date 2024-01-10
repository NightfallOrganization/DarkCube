/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class UICrafting extends InventoryUI {
    Set<Integer> inputSlots = Set.of(11, 20, 29);
    Set<Integer> resultSlots = Set.of(16, 25);
    Set<Integer> buttons = Set.of(23);

    public UICrafting(String name, Player p) {
        super(9, name, p);
        int row = 9;
        for (int i = 0; i < 9 * row; i++) {
            setInvSlot(new UIitemStack(true, new ItemStack(Material.BLACK_STAINED_GLASS_PANE)), i);
        }
        for (Set<Integer> set : Set.of(inputSlots, resultSlots)) {
            for (Integer i : set) {
                setInvSlot(new UIitemStack(false, new ItemStack(Material.AIR)), i);
            }
        }
        for (Integer i : buttons) {
            setInvSlot(new UIitemStack(true, new ItemStack(Material.OAK_BUTTON)), i);
        }
        //perhaps add crafting lvl?

    }

    @Override public boolean invClickEvent(InventoryClickEvent e) {
        if (buttons.contains(e.getSlot())) {
            if (e.getSlot() == 23) {
                for (int j : inputSlots){
                    if (e.getInventory().getItem(j) != null && e.getInventory().getItem(j).getType().equals(Material.IRON_INGOT)){
                        for (int i :resultSlots){
                            e.getInventory().setItem(i, new ItemStack(Material.IRON_BLOCK, 3));
                        }
                        for (int k : inputSlots){
                            e.getInventory().setItem(k, new ItemStack(Material.AIR));
                        }
                    }
                }

            }
        }
        //todo check here for confirm button
        return super.invClickEvent(e);
    }

    @Override public boolean invCloseEvent(InventoryCloseEvent e) {
        for (var j : Set.of(inputSlots, resultSlots)){
            for (int i : j) {
                var items = e.getInventory().getItem(i);
                if (items != null) {
                    var overflow = p.getInventory().addItem(items);
                    overflow.forEach((integer, itemStack) -> {
                        p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
                    });
                }
            }
        }


        return super.invCloseEvent(e);
    }
}
