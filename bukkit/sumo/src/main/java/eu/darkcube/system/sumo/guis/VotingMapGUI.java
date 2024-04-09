/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.guis;

import eu.darkcube.system.sumo.manager.MapManager;
import eu.darkcube.system.sumo.other.GameStates;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VotingMapGUI implements Listener {

    private final String TITLE = "§6Maps";
    private final GUIPattern guiPattern = new GUIPattern();
    private MapManager mapManager;

    public VotingMapGUI(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    public void openVotingMapGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, TITLE);

        guiPattern.setPattern(gui);

        // Papier auf Slot 4 setzen
        ItemStack book = new ItemStack(Material.PAPER);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(TITLE);
        bookMeta.setLore(java.util.Arrays.asList("§7Stimme für deine Lieblingsmaps!"));
        book.setItemMeta(bookMeta);
        gui.setItem(4, book);

        // Graue Wolle auf Slot 10 setzen
        ItemStack grayWool = new ItemStack(Material.WOOL, 1, (short) 7);
        ItemMeta grayWoolMeta = grayWool.getItemMeta();
        grayWoolMeta.setDisplayName("§aOrigin");
        grayWool.setItemMeta(grayWoolMeta);
        gui.setItem(10, grayWool);

        // Orangene Wolle auf Slot 11 setzen
        ItemStack orangeWool = new ItemStack(Material.WOOL, 1, (short) 1);
        ItemMeta orangeWoolMeta = orangeWool.getItemMeta();
        orangeWoolMeta.setDisplayName("§aAtlas");
        orangeWool.setItemMeta(orangeWoolMeta);
        gui.setItem(11, orangeWool);

        // Violetter Clay auf Slot 12 setzen
        ItemStack purpleClay = new ItemStack(Material.STAINED_CLAY, 1, (short) 10);
        ItemMeta purpleClayMeta = purpleClay.getItemMeta();
        purpleClayMeta.setDisplayName("§aDemonic");
        purpleClay.setItemMeta(purpleClayMeta);
        gui.setItem(12, purpleClay);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(TITLE)) {
            if (event.getCurrentItem() == null) return;

            Player player = (Player) event.getWhoClicked();
            ItemStack currentItem = event.getCurrentItem();

            if (currentItem.getType() == Material.WOOL && currentItem.getDurability() == 7) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 50f, 1f);
                player.sendMessage("§7Du hast für die Map §fOrigin §7gevotet");
                mapManager.voteForMap(player, "Origin");

            } else if (currentItem.getType() == Material.WOOL && currentItem.getDurability() == 1) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 50f, 1f);
                player.sendMessage("§7Du hast für die Map §fAtlas §7gevotet");
                mapManager.voteForMap(player, "Atlas");

            } else if (currentItem.getType() == Material.STAINED_CLAY && currentItem.getDurability() == 10) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 50f, 1f);
                player.sendMessage("§7Du hast für die Map §fDemonic §7gevotet");
                mapManager.voteForMap(player, "Demonic");
            }
        }
    }

}
