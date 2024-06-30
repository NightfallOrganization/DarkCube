/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.game;

import eu.darkcube.system.woolbattleteamfight.team.TeamManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorManager {

    private TeamManager teamManager;

    public ArmorManager(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public void setArmor(Player player) {
        if (!player.getWorld().getName().equals("world")) {
            return;  // Falls der Spieler sich nicht in der Welt "world" befindet
        }

        String team = teamManager.getPlayerTeam(player);

        Color armorColor;
        switch (team) {
            case "Red":
                armorColor = Color.fromRGB(153, 51, 51);
                break;
            case "Blue":
                armorColor = Color.fromRGB(51, 76, 178);
                break;
            case "Violet":
                armorColor = Color.fromRGB(127, 63, 178);
                break;
            case "Green":
                armorColor = Color.fromRGB(127, 204, 25);
                break;
            default:
                return;  // Falls der Spieler in keinem Team ist
        }

        player.getInventory().setHelmet(createColoredArmor(Material.LEATHER_HELMET, armorColor, "§aKopfbedeckung <3"));
        player.getInventory().setChestplate(createColoredArmor(Material.LEATHER_CHESTPLATE, armorColor, "§aHemd"));
        player.getInventory().setLeggings(createColoredArmor(Material.LEATHER_LEGGINGS, armorColor, "§aHöschen"));
        player.getInventory().setBoots(createColoredArmor(Material.LEATHER_BOOTS, armorColor, "§aSneakers"));
    }

    private ItemStack createColoredArmor(Material type, Color color, String name) {
        ItemStack item = new ItemStack(type);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        if (meta != null) {
            meta.setColor(color);
            meta.setDisplayName(name);
            meta.spigot().setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }

        return item;
    }

}
