package eu.darkcube.system.skyland;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class GetItem implements CommandExecutor {

    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (s instanceof Player) {
            Player player = (Player) s;
            Inventory inventory = Bukkit.createInventory(null, 9*4,
                    Component.text().content("               Items").color(TextColor.color(140, 202, 255)).build());

            inventory.setItem(1, CustomArmor.getEsdeathBootsItem());
            inventory.setItem(8, CustomArmor.getNetherblockHelmetItem());
            inventory.setItem(17, CustomArmor.getNetherblockChestplateItem());
            inventory.setItem(26, CustomArmor.getNetherblockLeggingsItem());
            inventory.setItem(35, CustomArmor.getNetherblockBootsItem());
            inventory.setItem(9, CustomArmor.getStarterSwordItem());
            inventory.setItem(10, CustomArmor.getNetherblockPickaxeItem());
            inventory.setItem(7, CustomArmor.getMiningHelmetItem());
            inventory.setItem(16, CustomArmor.getMiningChestplateItem());
            inventory.setItem(25, CustomArmor.getMiningLeggingsItem());
            inventory.setItem(34, CustomArmor.getMiningBootsItem());
            inventory.setItem(6, CustomArmor.getSpeedHelmetItem());
            inventory.setItem(15, CustomArmor.getSpeedChestplateItem());
            inventory.setItem(24, CustomArmor.getSpeedLeggingsItem());
            inventory.setItem(33, CustomArmor.getSpeedBootsItem());
            player.openInventory(inventory);

        } else {
            s.sendMessage("§7Du bist kein Spieler!");
        }

        return false;
    }
}