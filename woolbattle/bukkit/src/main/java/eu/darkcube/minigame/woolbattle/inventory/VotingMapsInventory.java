/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.inventory;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.Vote;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.util.data.Key;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static eu.darkcube.system.inventoryapi.item.ItemBuilder.item;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

public class VotingMapsInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle-voting-maps");
    private static final Key MAP = new Key(WoolBattle.instance(), "voting-map");

    public VotingMapsInventory(WBUser user) {
        super(TYPE, Message.INVENTORY_VOTING_MAPS.getMessage(user), user);
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null)
            return;
        String mapName = ItemManager.getId(event.item(), MAP);
        if (mapName == null)
            return;
        Map map = WoolBattle.instance().mapManager().getMap(mapName);
        Vote<Map> vote = WoolBattle.instance().lobby().VOTES_MAP.get(user);
        if (vote != null) {
            if (map.equals(vote.vote)) {
                user.user().sendMessage(Message.ALREADY_VOTED_FOR_MAP, mapName);
                return;
            }
        }
        vote = new Vote<>(System.currentTimeMillis(), map);
        WoolBattle.instance().lobby().VOTES_MAP.put(user, vote);
        WoolBattle.instance().lobby().recalculateMap();
        user.user().sendMessage(Message.VOTED_FOR_MAP, mapName);
        recalculate();
    }

    @Override
    protected void fillItems(java.util.Map<Integer, ItemStack> items) {
        List<Map> maps =
                WoolBattle.instance().mapManager().getMaps().stream().filter(Map::isEnabled)
                        .collect(Collectors.toList());
        Vote<Map> vote = WoolBattle.instance().lobby().VOTES_MAP.get(user);
        for (int i = 0; i < maps.size(); i++) {
            Map map = maps.get(i);
            ItemBuilder bu = item(map.getIcon().getMaterial()).damage(map.getIcon().getId());
            ItemManager.setId(bu, MAP, map.getName());
            if (vote != null)
                if (map.equals(vote.vote))
                    bu.glow(true);
            bu.displayname(text(map.getName()).color(NamedTextColor.GREEN));
            items.put(i, bu.build());
        }
    }

    @Override
    protected void insertFallbackItems() {
        super.insertFallbackItems();
        fallbackItems.put(IInventory.slot(1, 5), Item.LOBBY_VOTING_MAPS.getItem(user));
    }
}
