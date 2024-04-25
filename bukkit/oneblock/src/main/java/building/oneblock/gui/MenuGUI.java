/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.gui;

import building.oneblock.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuGUI {
    private PlayerManager playerManager;
    private final String menuGUITitle = "§f\udaff\udfefⲈ";

    public MenuGUI(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, menuGUITitle);
        initializeItems(gui, player);
        player.openInventory(gui);
    }

    private void initializeItems(Inventory inventory, Player player) {

//        inventory.setItem(13, SomeCustomItem.getCustomItem(player, playerManager));
    }

    public String getMenuGUITitle() {
        return menuGUITitle;
    }
}
