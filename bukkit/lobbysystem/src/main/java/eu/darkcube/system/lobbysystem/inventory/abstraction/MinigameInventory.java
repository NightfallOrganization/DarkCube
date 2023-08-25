/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.inventory.abstraction;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.service.CloudServiceUpdateEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.server.ServerInformation;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MinigameInventory extends LobbyAsyncPagedInventory {
    public static final Key minigameServer = new Key(Lobby.getInstance(), "minigameserver");
    private boolean done = false;
    private Item minigameItem;
    private Listener listener = new Listener();

    public MinigameInventory(Component title, Item minigameItem, InventoryType type, User user) {
        super(type, title, user);
        this.minigameItem = minigameItem;
        this.done = true;
        this.complete();
        InjectionLayer.boot().instance(EventManager.class).registerListener(this.listener);
    }

    protected abstract Set<String> getCloudTasks();

    @Override protected boolean done() {
        return super.done() && this.done;
    }

    @Override protected final void destroy() {
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(this.listener);
        this.destroy0();
        super.destroy();
    }

    protected void destroy0() {
    }

    @Override protected void fillItems(Map<Integer, ItemStack> items) {
        Lobby lobby = Lobby.getInstance();

        List<ServerInformation> informations = lobby
                .serverManager()
                .informations()
                .stream()
                .filter(ServerInformation.filterByTask(getCloudTasks()))
                .filter(ServerInformation.ONLINE_FILTER)
                .collect(Collectors.toList());

        List<ItemSortingInfo> itemSortingInfos = new ArrayList<>();
        for (ServerInformation information : informations) {

            int onlinePlayers = information.onlinePlayers();
            int maxPlayers = information.maxPlayers();
            GameState state = information.gameState();

            Component displayName = information.displayName();
            ItemBuilder builder = ItemBuilder.item(Material.STAINED_CLAY);
            builder.amount(onlinePlayers);
            builder.displayname(displayName);
            builder.lore(Component.text("Spieler: " + onlinePlayers + "/" + maxPlayers, NamedTextColor.GRAY));
            if (state == GameState.LOBBY) {
                if (onlinePlayers == 0) {
                    builder.damage(DyeColor.GRAY.getWoolData());
                } else {
                    builder.damage(DyeColor.LIME.getWoolData());
                }
                builder.lore(Message.GAMESERVER_STATE.getMessage(user.getUser(), Message.STATE_LOBBY));
            } else if (state == GameState.INGAME) {
                builder.damage(DyeColor.ORANGE.getWoolData());
                builder.lore(Message.GAMESERVER_STATE.getMessage(user.getUser(), Message.STATE_INGAME));
            } else {
                throw new IllegalStateException();
            }
            builder.persistentDataStorage().set(minigameServer, PersistentDataTypes.UUID, information.uniqueId());
            ItemStack item = builder.build();
            ItemSortingInfo info = new ItemSortingInfo(item, information);
            itemSortingInfos.add(info);
        }

        itemSortingInfos.sort((o1, o2) -> ServerInformation.COMPARATOR.compare(o1.information(), o2.information()));

        int size = itemSortingInfos.size();
        for (int slot = 0; slot < size; slot++) {
            items.put(slot, itemSortingInfos.get(slot).item());
        }
    }

    @Override protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 5), this.minigameItem.getItem(this.user.getUser()));
        super.insertFallbackItems();
    }

    protected static class ItemSortingInfo {

        private ItemStack item;
        private ServerInformation information;

        public ItemSortingInfo(ItemStack item, ServerInformation information) {
            this.item = item;
            this.information = information;
        }

        public ServerInformation information() {
            return information;
        }

        public ItemStack item() {
            return item;
        }
    }

    public class Listener {
        @EventListener public void handle(CloudServiceUpdateEvent ignored) {
            MinigameInventory.this.recalculate();
        }
    }
}
