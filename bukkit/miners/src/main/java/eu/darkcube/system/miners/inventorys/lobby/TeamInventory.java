/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.inventorys.lobby;

import static eu.darkcube.system.miners.enums.InventoryItems.*;
import static eu.darkcube.system.miners.enums.Names.TEAMS;
import static eu.darkcube.system.miners.utils.message.Message.TEAM_JOIN;

import java.util.function.Function;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.enums.InventoryItems;
import eu.darkcube.system.miners.utils.Team;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamInventory implements TemplateInventoryListener {
    private final InventoryTemplate inventoryTemplate;

    public String getKeyValue() {
        return inventoryTemplate.key().value();
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public TeamInventory() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(Miners.getInstance(), "teams"), 45);
        inventoryTemplate.title(TEAMS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, HOTBAR_ITEM_TEAMS);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);

        Team teamRed = Miners.getInstance().getTeamRed();
        Team teamBlue = Miners.getInstance().getTeamBlue();
        Team teamGreen = Miners.getInstance().getTeamGreen();
        Team teamYellow = Miners.getInstance().getTeamYellow();
        Team teamWhite = Miners.getInstance().getTeamWhite();
        Team teamBlack = Miners.getInstance().getTeamBlack();
        Team teamPurple = Miners.getInstance().getTeamPurple();
        Team teamOrange = Miners.getInstance().getTeamOrange();

        setTeamItem(21, teamRed, getDisplayItem(INVENTORY_TEAM_RED, teamRed));
        setTeamItem(23, teamBlue, getDisplayItem(INVENTORY_TEAM_BLUE, teamBlue));
        setTeamItem(24, teamGreen, getDisplayItem(INVENTORY_TEAM_GREEN, teamGreen));
        setTeamItem(20, teamYellow, getDisplayItem(INVENTORY_TEAM_YELLOW, teamYellow));
        setTeamItem(30, teamWhite, getDisplayItem(INVENTORY_TEAM_WHITE, teamWhite));
        setTeamItem(32, teamBlack, getDisplayItem(INVENTORY_TEAM_BLACK, teamBlack));
        setTeamItem(33, teamPurple, getDisplayItem(INVENTORY_TEAM_PURPLE, teamPurple));
        setTeamItem(29, teamOrange, getDisplayItem(INVENTORY_TEAM_ORANGE, teamOrange));

        inventoryTemplate.addListener(this);
    }

    private void setTeamItem(int slot, Team team, Function<User, Object> item) {
        if (team.isActive()) {
            inventoryTemplate.setItem(1, slot, item);
        }
    }

    private Function<User, Object> getDisplayItem(InventoryItems item, Team team) {
        return user -> {
            Player player = Bukkit.getPlayer(user.uniqueId());
            if (Team.isPlayerInTeam(team, player)) {
                return item.getItem(user).glow(true);
            } else {
                return item.getItem(user);
            }
        };
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String clickedInventoryItem = InventoryItems.getItemID(item);
        if (clickedInventoryItem == null) return;
        Player player = Bukkit.getPlayer(user.uniqueId());
        player.sendMessage("§7onClick");

        onClickTeam(INVENTORY_TEAM_RED, Miners.getInstance().getTeamRed(), clickedInventoryItem, player, user);
        onClickTeam(INVENTORY_TEAM_BLUE, Miners.getInstance().getTeamRed(), clickedInventoryItem, player, user);
        onClickTeam(INVENTORY_TEAM_GREEN, Miners.getInstance().getTeamRed(), clickedInventoryItem, player, user);
        onClickTeam(INVENTORY_TEAM_YELLOW, Miners.getInstance().getTeamRed(), clickedInventoryItem, player, user);
        onClickTeam(INVENTORY_TEAM_WHITE, Miners.getInstance().getTeamRed(), clickedInventoryItem, player, user);
        onClickTeam(INVENTORY_TEAM_BLACK, Miners.getInstance().getTeamRed(), clickedInventoryItem, player, user);
        onClickTeam(INVENTORY_TEAM_PURPLE, Miners.getInstance().getTeamRed(), clickedInventoryItem, player, user);
        onClickTeam(INVENTORY_TEAM_ORANGE, Miners.getInstance().getTeamRed(), clickedInventoryItem, player, user);

        inventory.pagedController().publishUpdatePage();
    }

    private void onClickTeam(InventoryItems inventoryItems, Team team, String clickedInventoryItem, Player player, User user) {
        player.sendMessage("§7onClickTeam");
        if (clickedInventoryItem.equals(inventoryItems.itemID())) {
            team.addPlayer(player);
            user.sendMessage(TEAM_JOIN, team.getName());
        }

    }
}
