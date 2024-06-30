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
import building.oneblock.manager.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuEditGUI {
    private final String menuEditGUITitle = "§f\udaff\udfefⲊ";

    public void openMenuEditGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, menuEditGUITitle);
        initializeItems(gui, player);
        player.openInventory(gui);
    }

    public void closeMenuEditGUI(Player player) {
        player.closeInventory();
    }

    private void initializeItems(Inventory inventory, Player player) {

//        inventory.setItem(10, Button1.createButton1());
//        inventory.setItem(11, Button1.createButton1());
//        inventory.setItem(12, Button1.createButton1());
//        inventory.setItem(13, Button1.createButton1());
//        inventory.setItem(14, Button2.createButton2());
//        inventory.setItem(16, PlayerHeadItem.getPlayerHeadItem(player, playerManager));

    }

    public String getMenuGUITitle() {
        return menuEditGUITitle;
    }
}
