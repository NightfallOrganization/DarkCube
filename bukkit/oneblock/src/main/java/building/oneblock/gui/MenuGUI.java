/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.gui;

import building.oneblock.items.gui.PlayerHeadItem;
import building.oneblock.items.gui.buttons.Button1;
import building.oneblock.items.gui.buttons.Button2;
import building.oneblock.manager.WorldSlotManager;
import building.oneblock.manager.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuGUI {
    private PlayerManager playerManager;
    private WorldSlotManager worldSlotManager;
    private final String menuGUITitle = "§f\udaff\udfefⲈ";

    public MenuGUI(PlayerManager playerManager, WorldSlotManager worldSlotManager) {
        this.playerManager = playerManager;
        this.worldSlotManager = worldSlotManager;
    }

    public void openMenuGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, menuGUITitle);
        worldSlotManager.loadSlots(player);
        initializeItems(gui, player);
        player.openInventory(gui);
    }

    public void closeMenuGUI(Player player) {
        player.closeInventory();
    }

    private void initializeItems(Inventory inventory, Player player) {

        for (int slot = 0; slot < WorldSlotManager.MAX_SLOTS; slot++) {
            String worldName = worldSlotManager.getWorld(slot);
            ItemStack buttonItem;

            if ("empty".equals(worldName)) {
                buttonItem = Button1.createButton1();
            } else {
                buttonItem = Button2.createButton2(worldName);
            }

            inventory.setItem(10 + slot, buttonItem);
        }

        inventory.setItem(16, PlayerHeadItem.getPlayerHeadItem(player, playerManager));

    }


    public String getMenuGUITitle() {
        return menuGUITitle;
    }
}
