/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.executions;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.other.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.ChatColor;
import java.util.UUID;

public class EquipPlayer {
    private TeamManager teamManager;

    public EquipPlayer(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public void equipPlayerIfInTeam(Player player) {
        UUID playerId = player.getUniqueId();
        ChatColor teamColor = teamManager.getPlayerTeam(playerId);

        if (teamColor != null) {
            equipWithColoredArmor(player, teamColor);
        }
    }

    private void equipWithColoredArmor(Player player, ChatColor teamColor) {
        PlayerInventory inventory = player.getInventory();
        User user = UserAPI.instance().user(player.getUniqueId());
        String itemHelmName = Message.ITEM_HELM.convertToString(user);
        String itemChestplateName = Message.ITEM_CHESTPLATE.convertToString(user);
        String itemLeggingsName = Message.ITEM_LEGGINGS.convertToString(user);
        String itemBootsName = Message.ITEM_BOOTS.convertToString(user);

        ChatColor nameColor = teamColor == TeamManager.TEAM_BLACK ? ChatColor.DARK_GRAY : teamColor;
        ItemStack helmet = createColoredArmorItem(Material.LEATHER_HELMET, teamColor, nameColor + itemHelmName);
        ItemStack chestplate = createColoredArmorItem(Material.LEATHER_CHESTPLATE, teamColor, nameColor + itemChestplateName);
        ItemStack leggings = createColoredArmorItem(Material.LEATHER_LEGGINGS, teamColor, nameColor + itemLeggingsName);
        ItemStack boots = createColoredArmorItem(Material.LEATHER_BOOTS, teamColor, nameColor + itemBootsName);

        inventory.setHelmet(helmet);
        inventory.setChestplate(chestplate);
        inventory.setLeggings(leggings);
        inventory.setBoots(boots);
    }

    private ItemStack createColoredArmorItem(Material material, ChatColor teamColor, String name) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        meta.setDisplayName(name);
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (teamColor == TeamManager.TEAM_BLACK) {
            meta.setColor(org.bukkit.Color.fromRGB(30, 30, 30));
        } else if (teamColor == TeamManager.TEAM_WHITE) {
            meta.setColor(org.bukkit.Color.fromRGB(255, 255, 255));
        }

        item.setItemMeta(meta);
        return item;
    }
}
