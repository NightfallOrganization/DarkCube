/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import eu.darkcube.system.citybuild.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.IOException;

public class BagListener implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey bagKey;

    public BagListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.bagKey = new NamespacedKey(plugin, "ordinary_bag_data");
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == 1) {
            Inventory bagInventory = Bukkit.createInventory(null, 18, "§f\udaff\udfefḀ");

            // Items aus dem Persistent Data Container laden
            if(item.getItemMeta().getPersistentDataContainer().has(bagKey, PersistentDataType.STRING)) {
                try {
                    ItemStack[] contents = ItemStackUtils.itemStackArrayFromBase64(item.getItemMeta().getPersistentDataContainer().get(bagKey, PersistentDataType.STRING));
                    bagInventory.setContents(contents);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            player.openInventory(bagInventory);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();

            // Überprüfen, ob das angeklickte oder das Cursor-Item ein Beutel ist
            boolean clickedIsBag = clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasCustomModelData() && clickedItem.getItemMeta().getCustomModelData() == 1;
            boolean cursorIsBag = cursorItem != null && cursorItem.hasItemMeta() && cursorItem.getItemMeta().hasCustomModelData() && cursorItem.getItemMeta().getCustomModelData() == 1;

            // Wenn beide, das angeklickte Item und das Item auf dem Cursor, Beutel sind, dann verhindere das Stapeln
            if (clickedIsBag && cursorIsBag) {
                event.setCancelled(true);
                player.sendMessage("§cDu kannst diesen Beutel nicht stapeln!");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("§f\udaff\udfefḀ")) {
            Player player = (Player) event.getPlayer();

            // Prüfe, ob sich das Taschenitem in der Haupt- oder Nebenhand des Spielers befindet
            ItemStack bagItemInMainHand = player.getInventory().getItemInMainHand();
            ItemStack bagItemInOffHand = player.getInventory().getItemInOffHand();

            ItemStack currentBagItem = null;
            if (bagItemInMainHand != null && bagItemInMainHand.hasItemMeta() && bagItemInMainHand.getItemMeta().hasCustomModelData() && bagItemInMainHand.getItemMeta().getCustomModelData() == 1) {
                currentBagItem = bagItemInMainHand;
            } else if (bagItemInOffHand != null && bagItemInOffHand.hasItemMeta() && bagItemInOffHand.getItemMeta().hasCustomModelData() && bagItemInOffHand.getItemMeta().getCustomModelData() == 1) {
                currentBagItem = bagItemInOffHand;
            }

            if (currentBagItem != null) {
                try {
                    String data = ItemStackUtils.itemStackArrayToBase64(event.getInventory().getContents());
                    ItemMeta meta = currentBagItem.getItemMeta();
                    meta.getPersistentDataContainer().set(bagKey, PersistentDataType.STRING, data);
                    currentBagItem.setItemMeta(meta);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
