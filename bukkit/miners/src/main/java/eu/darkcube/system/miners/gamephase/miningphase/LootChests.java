package eu.darkcube.system.miners.gamephase.miningphase;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class LootChests {

    public static void createLootChest(Location location) {
        location.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) location.getBlock().getState();
        chest.getBlockInventory().setContents(generateChestContent());
        chest.update(true);
    }

    public static ItemStack[] generateChestContent() {
        ItemStack[] items = new ItemStack[27];
        return new ItemStack[0];
    }

    public static class LootItem {
        //// TODO
    }

}
