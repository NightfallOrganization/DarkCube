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
import eu.darkcube.system.sumo.prefix.PrefixManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VotingGUI implements Listener {

    private final String TITLE = "§6Voting";
    private final GUIPattern guiPattern = new GUIPattern();
    private VotingMapGUI votingMapGUI;

    public VotingGUI(VotingMapGUI votingMapGUI) {
        this.votingMapGUI = votingMapGUI;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            Player player = event.getPlayer();
            ItemStack itemInHand = player.getItemInHand();

            if (player.getInventory().getHeldItemSlot() == 5 && itemInHand.getType() == Material.PAPER) {
                openVotingGUI(player);
                event.setCancelled(true);
            }
        }
    }

    public void openVotingGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, TITLE);

        guiPattern.setPattern(gui);

        // Papier auf Slot 4 setzen
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName(TITLE);
        paperMeta.setLore(java.util.Arrays.asList("§7Stimme für Spielszenarien ab!"));
        paper.setItemMeta(paperMeta);
        gui.setItem(4, paper);

        // Papier auf Slot 22 setzen
        ItemStack mappaper = new ItemStack(Material.PAPER);
        ItemMeta mappaperMeta = mappaper.getItemMeta();
        mappaperMeta.setDisplayName("§6Maps");
        mappaperMeta.setLore(java.util.Arrays.asList("§7Stimme für deine Lieblingsmaps!"));
        mappaper.setItemMeta(mappaperMeta);
        gui.setItem(22, mappaper);

        player.openInventory(gui);

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(TITLE)) {

            if (event.getCurrentItem() == null) return;

            Player player = (Player) event.getWhoClicked();
            ItemStack itemInHand = player.getItemInHand();

            if (itemInHand.getType() == Material.PAPER) {
                votingMapGUI.openVotingMapGUI(player);
            }

        }
    }

}
