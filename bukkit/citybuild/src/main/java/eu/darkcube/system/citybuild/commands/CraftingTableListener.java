package eu.darkcube.system.citybuild.commands;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class CraftingTableListener implements Listener {


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CRAFTING_TABLE) {
            event.setCancelled(true);  // Verhindert das normale Öffnen des Crafting Tables

            Player player = event.getPlayer();
            Inventory inv = Bukkit.createInventory(null, InventoryType.WORKBENCH, "§f\uDAFF\uDFDAḆ");
            player.openInventory(inv);
        }
    }
}
