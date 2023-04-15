package eu.darkcube.system.skyland.inventoryUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUI {

    UIitemStack[][] inv;
    String name;

    public InventoryUI(){

    }

    public void openInv(Player p, int size){
        //Inventory inventory = Bukkit.createInventory(p, inv.length*inv[0].length, name);
        Inventory inventory = Bukkit.createInventory(p, size, "name");

        for (UIitemStack[] row: inv) {
            for (UIitemStack uiis:row) {
                inventory.addItem(uiis.getItemStack());
            }
        }



        p.openInventory(inventory);
    }



}
