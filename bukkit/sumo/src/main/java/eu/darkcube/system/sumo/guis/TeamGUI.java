/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.guis;

import eu.darkcube.system.sumo.executions.EquipPlayer;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.other.GameStates;
import eu.darkcube.system.sumo.other.Message;
import eu.darkcube.system.sumo.prefix.PrefixManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
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

public class TeamGUI implements Listener {

    private final String TITLE = "§9Teams";
    private final GUIPattern guiPattern = new GUIPattern();
    private PrefixManager prefixManager;
    private EquipPlayer equipPlayer;
    TeamManager teamManager;

    public TeamGUI(TeamManager teamManager, PrefixManager prefixManager, EquipPlayer equipPlayer) {
        this.teamManager = teamManager;
        this.prefixManager = prefixManager;
        this.equipPlayer = equipPlayer;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            Player player = event.getPlayer();
            ItemStack itemInHand = player.getItemInHand();

            if (player.getInventory().getHeldItemSlot() == 3 && itemInHand.getType() == Material.BOOK) {
                openTeamGUI(player);
                event.setCancelled(true);
            }
        }
    }

    public void openTeamGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, TITLE);
        User user = UserAPI.instance().user(player.getUniqueId());
        String itemTeamLoreName = Message.ITEM_LORE_TEAMS_BOOK.convertToString(user);
        guiPattern.setPattern(gui);

        // Buch auf Slot 4 setzenw1
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(TITLE);
        bookMeta.setLore(java.util.Arrays.asList(itemTeamLoreName));
        book.setItemMeta(bookMeta);
        gui.setItem(4, book);

        // Schwarze Wolle auf Slot 21 setzen
        ItemStack blackWool = new ItemStack(Material.WOOL, 1, (short) 15); // 15 ist der Datenwert für schwarze Wolle
        ItemMeta blackWoolMeta = blackWool.getItemMeta();
        blackWoolMeta.setDisplayName("§8Schwarz");
        blackWool.setItemMeta(blackWoolMeta);
        gui.setItem(21, blackWool);

        // Weiße Wolle auf Slot 23 setzen
        ItemStack whiteWool = new ItemStack(Material.WOOL, 1, (short) 0); // 0 ist der Datenwert für weiße Wolle
        ItemMeta whiteWoolMeta = whiteWool.getItemMeta();
        whiteWoolMeta.setDisplayName("§fWeiß");
        whiteWool.setItemMeta(whiteWoolMeta);
        gui.setItem(23, whiteWool);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(TITLE)) {

            if (event.getCurrentItem() == null) return;

            Player player = (Player) event.getWhoClicked();

            // Überprüfen, ob schwarze Wolle angeklickt wurde
            if (event.getCurrentItem().getType() == Material.WOOL && event.getCurrentItem().getDurability() == 15) {
                teamManager.setPlayerTeam(player, TeamManager.TEAM_BLACK);
                equipPlayer.equipPlayerIfInTeam(player);
                player.sendMessage("§7Du bist jetzt im Team §8Schwarz");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 50f, 1f);
            }

            // Überprüfen, ob weiße Wolle angeklickt wurde
            else if (event.getCurrentItem().getType() == Material.WOOL && event.getCurrentItem().getDurability() == 0) {
                teamManager.setPlayerTeam(player, TeamManager.TEAM_WHITE);
                equipPlayer.equipPlayerIfInTeam(player);
                player.sendMessage("§7Du bist jetzt im Team §fWeiß");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 50f, 1f);
            }
        }
    }

}
