/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.guis;

import eu.darkcube.system.woolbattleteamfight.team.MapTeamSpawns;
import eu.darkcube.system.woolbattleteamfight.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeamGUI implements Listener {

    private TeamManager teamManager;
    private MapTeamSpawns mapTeamSpawns;

    public TeamGUI(TeamManager teamManager, MapTeamSpawns mapTeamSpawns) {
        this.teamManager = teamManager;
        this.mapTeamSpawns = mapTeamSpawns;
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
                    case 11: // Blau
                        if (!teamManager.isTeamFull("Blue")) {
                            teamManager.addToTeam(player, "Blue", teamManager);
                            player.sendMessage(ChatColor.GRAY + "Du bist nun in Team" + ChatColor.BLUE + " Blau");
                        } else {
                            player.sendMessage(ChatColor.RED + "Das blaue Team ist voll!");
                        }
                        break;
                    case 14: // Rot
                        if (!teamManager.isTeamFull("Red")) {
                            teamManager.addToTeam(player, "Red", teamManager);
                            player.sendMessage(ChatColor.GRAY + "Du bist nun in Team" + ChatColor.RED + " Rot");
                        } else {
                            player.sendMessage(ChatColor.RED + "Das rote Team ist voll!");
                        }
                        break;
                    case 10: // Violett
                        if (!teamManager.isTeamFull("Violet")) {
                            teamManager.addToTeam(player, "Violet", teamManager);
                            player.sendMessage(ChatColor.GRAY + "Du bist nun in Team" + ChatColor.DARK_PURPLE + " Violett");
                        } else {
                            player.sendMessage(ChatColor.RED + "Das violette Team ist voll!");
                        }
                        break;
                    case 5: // Grün
                        if (!teamManager.isTeamFull("Green")) {
                            teamManager.addToTeam(player, "Green", teamManager);
                            player.sendMessage(ChatColor.GRAY + "Du bist nun in Team" + ChatColor.DARK_GREEN + " Grün");
                        } else {
                            player.sendMessage(ChatColor.RED + "Das grüne Team ist voll!");
                        }
                        break;
                }
            }
        }
    }


    public static void openTeamsInventory(Player player) {
        Inventory teamsInv = Bukkit.createInventory(null, 45, ChatColor.BLUE + "Teams");

        // Slotnummern für graue Glasscheiben
        int[] graySlots = {0, 1, 2, 3, 5, 6, 7, 8, 12, 14, 18, 22, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44};
        for (int slot : graySlots) {
            teamsInv.setItem(slot, createGlassPane(DyeColor.GRAY));
        }

// Slotnummern für dunkelgraue Glasscheiben
        int[] darkGraySlots = {9, 10, 11, 13, 15, 16, 17, 19, 20, 21, 23, 24, 25, 27, 29, 31, 33, 35, 37, 39, 41, 43};
        for (int slot : darkGraySlots) {
            teamsInv.setItem(slot, createGlassPane(DyeColor.BLACK));
        }

        // Buch mit gewünschtem Namen und Lore erstellen
        ItemStack book = createItem(Material.BOOK, ChatColor.BLUE + "Teams", ChatColor.GRAY + "Wähle dein Team!", ChatColor.GRAY + "Möge das beste Team gewinnen!");
        teamsInv.setItem(4, book); // Platziere das Buch im Slot 5

        // Hinzufügen von Wolle für verschiedene Teams
        teamsInv.setItem(20, createItem(Material.WOOL, ChatColor.BLUE + "Blau", (short) 11));
        teamsInv.setItem(21, createItem(Material.WOOL, ChatColor.RED + "Rot", (short) 14));
        teamsInv.setItem(23, createItem(Material.WOOL, ChatColor.DARK_PURPLE + "Violett", (short) 10));
        teamsInv.setItem(24, createItem(Material.WOOL, ChatColor.DARK_GREEN + "Grün", (short) 5));

        player.openInventory(teamsInv);
    }

    private static ItemStack createGlassPane(DyeColor color) {
        Material material = Material.STAINED_GLASS_PANE;
        short colorData;
        switch (color) {
            case GRAY:
                colorData = 7; // Datenwert für grau
                break;
            case BLACK: // Verwenden Sie BLACK für dunkelgrau, da es keinen DyeColor für dunkelgrau gibt
                colorData = 15; // Datenwert für schwarz (dunkelgrau in diesem Fall)
                break;
            default:
                colorData = 0; // Falls das Standardverhalten gefragt ist
        }
        ItemStack pane = new ItemStack(material, 1, colorData);
        return pane;
    }

    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);

            List<String> loreList = new ArrayList<>();
            for (String line : lore) {
                loreList.add(line);
            }

            meta.setLore(loreList);
            item.setItemMeta(meta);
        }

        return item;
    }

    private static ItemStack createItem(Material material, String name, short colorCode) {
        ItemStack item = new ItemStack(material, 1, colorCode);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }

}