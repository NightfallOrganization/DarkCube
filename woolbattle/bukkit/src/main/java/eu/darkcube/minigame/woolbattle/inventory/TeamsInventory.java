/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.inventory;

import java.util.Map;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class TeamsInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle-teams");
    private final Key TEAM = new Key(woolbattle, "team_id");
    private final TeamsListener listener = new TeamsListener();

    public TeamsInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.INVENTORY_TEAMS.getMessage(user), user);
        Bukkit.getPluginManager().registerEvents(listener, woolbattle);
        complete();
    }

    @Override protected boolean done() {
        return super.done() && TEAM != null;
    }

    @Override protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        String teamId = ItemManager.getId(event.item(), TEAM);
        if (teamId == null) return;
        Team team = woolbattle.teamManager().getTeam(teamId);
        if (team.equals(user.getTeam())) {
            user.user().sendMessage(Message.ALREADY_IN_TEAM);
            return;
        }
        if (team.getUsers().size() >= team.getType().getMaxPlayers()) {
            user.user().sendMessage(Message.TEAM_IS_FULL, team.getName(user.user()));
            return;
        }
        user.setTeam(team);
        user.user().sendMessage(Message.CHANGED_TEAM, team.getName(user.user()));
        Bukkit.getPluginManager().callEvent(new Refresh());
    }

    @Override protected void fillItems(Map<Integer, ItemStack> items) {
        int i = 0;
        for (Team team : woolbattle.teamManager().getTeams()) {
            if (team.isSpectator()) continue;
            ItemBuilder b = ItemBuilder.item(Material.WOOL);
            b.displayname(team.getName(user.user()));
            b.damage(team.getType().getWoolColor().getWoolData());
            if (team.getUsers().contains(user)) {
                b.glow(true);
            }
            team.getUsers().forEach(u -> b.lore(u.getTeamPlayerName()));
            ItemManager.setId(b, TEAM, team.getType().getDisplayNameKey());
            items.put(i++, b.build());
        }
    }

    @Override protected void insertFallbackItems() {
        fallbackItems.put(IInventory.slot(1, 5), Item.LOBBY_TEAMS.getItem(user));
        super.insertFallbackItems();
    }

    @Override protected void destroy() {
        HandlerList.unregisterAll(listener);
        super.destroy();
    }

    public static class Refresh extends Event {
        private static final HandlerList handlers = new HandlerList();

        public Refresh() {
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }

        @Override public HandlerList getHandlers() {
            return handlers;
        }
    }

    private class TeamsListener implements Listener {
        @EventHandler public void handle(Refresh event) {
            recalculate();
        }
    }
}
