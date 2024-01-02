/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.guis;

import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamGUI implements Listener {

    private final String TITLE = "§9Teams";
    private final GUIPattern guiPattern = new GUIPattern();
    private final TeamManager teamManager;

    public TeamGUI(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();

        // Überprüft, ob der Spieler das Buch anklickt
        if (itemInHand != null && itemInHand.getType() == Material.BOOK && itemInHand.hasItemMeta() && "§9Teams".equals(itemInHand.getItemMeta().getDisplayName())) {
            openTeamGUI(player);
            event.setCancelled(true); // Verhindert den normalen Gebrauch des Buches
        }
    }

    public void openTeamGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, TITLE);

        // Setze das GUI Pattern
        guiPattern.setPattern(gui);

        // Buch auf Slot 4 setzen
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(TITLE);
        bookMeta.setLore(java.util.Arrays.asList("§7Wähle dein Team!"));
        book.setItemMeta(bookMeta);
        gui.setItem(4, book);

        // Schwarze Wolle auf Slot 21 setzen
        ItemStack blackWool = new ItemStack(Material.WOOL, 1, (short) 15); // 15 ist die Datenwert für schwarze Wolle
        ItemMeta blackWoolMeta = blackWool.getItemMeta();
        blackWoolMeta.setDisplayName("§8Schwarz");
        blackWool.setItemMeta(blackWoolMeta);
        gui.setItem(21, blackWool);

        // Weiße Wolle auf Slot 23 setzen
        ItemStack whiteWool = new ItemStack(Material.WOOL, 1, (short) 0); // 0 ist die Datenwert für weiße Wolle
        ItemMeta whiteWoolMeta = whiteWool.getItemMeta();
        whiteWoolMeta.setDisplayName("§fWeiß");
        whiteWool.setItemMeta(whiteWoolMeta);
        gui.setItem(23, whiteWool);

        // GUI für den Spieler öffnen
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BLUE + "Teams")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            if (clickedItem.getType() == Material.WOOL) {
                switch (clickedItem.getDurability()) {
                    case 15: // Schwarz
                        if (teamManager.isPlayerInTeam(player)) {
                            teamManager.removePlayerFromTeam(player);
                        }

                        if (!teamManager.isTeamFull("Black")) {
                            teamManager.addToTeam(player, "Black", teamManager);
                            player.sendMessage(ChatColor.GRAY + "Du bist nun in Team" + ChatColor.DARK_GRAY + " Schwarz");
                        } else {
                            player.sendMessage(ChatColor.RED + "Das schwarze Team ist voll!");
                        }
                        break;
                    case 0: // Weiß
                        if (teamManager.isPlayerInTeam(player)) {
                            teamManager.removePlayerFromTeam(player);
                        }

                        if (!teamManager.isTeamFull("White")) {
                            teamManager.addToTeam(player, "White", teamManager);
                            player.sendMessage(ChatColor.GRAY + "Du bist nun in Team" + ChatColor.WHITE + " Weiß");
                        } else {
                            player.sendMessage(ChatColor.RED + "Das weiße Team ist voll!");
                        }
                        break;
                    // Du kannst weitere Farbcodes hier hinzufügen, falls benötigt
                }
            }
        }
    }

}
