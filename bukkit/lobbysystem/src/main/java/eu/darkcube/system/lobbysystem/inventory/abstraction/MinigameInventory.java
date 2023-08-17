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
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.MinigameServerSortingInfo;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    @SuppressWarnings("deprecation") @Override protected void fillItems(Map<Integer, ItemStack> items) {

        Collection<ServiceInfoSnapshot> servers = new HashSet<>();
        getCloudTasks().forEach(task -> servers.addAll(InjectionLayer.boot().instance(CloudServiceProvider.class).servicesByTask(task)));

        Map<ServiceInfoSnapshot, GameState> states = new HashMap<>();
        for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
            try {
                GameState state = server.readProperty(DarkCubeServiceProperty.GAME_STATE);
                if (state == null || state == GameState.UNKNOWN) continue;
                if (state == GameState.STOPPING) {
                    continue;
                }
                states.put(server, state);
            } catch (Exception ex) {
                servers.remove(server);
            }
        }
        List<ItemSortingInfo> itemSortingInfos = new ArrayList<>();
        for (ServiceInfoSnapshot server : new ArrayList<>(servers)) {

            int playingPlayers = server.readProperty(DarkCubeServiceProperty.PLAYING_PLAYERS);
            int maxPlayingPlayers = server.readProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS);

            GameState state = states.get(server);
            String motd = server.readProperty(DarkCubeServiceProperty.DISPLAY_NAME);
            if (motd == null || motd.toLowerCase().contains("loading") || state == null) {
                servers.remove(server);
                states.remove(server);
                continue;
            }
            ItemBuilder builder = ItemBuilder.item(Material.STAINED_CLAY);
            builder.amount(playingPlayers);
            builder.displayname(motd);
            builder.lore("§7Spieler: " + playingPlayers + "/" + maxPlayingPlayers);
            if (state == GameState.LOBBY) {
                if (playingPlayers == 0) {
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
            ItemStack item = builder.build();
            item = ItemBuilder
                    .item(item)
                    .persistentDataStorage()
                    .iset(minigameServer, PersistentDataTypes.STRING, server.serviceId().uniqueId().toString())
                    .builder()
                    .build();
            ItemSortingInfo info = new ItemSortingInfo(item, playingPlayers, maxPlayingPlayers, state);
            itemSortingInfos.add(info);
        }

        Collections.sort(itemSortingInfos);
        int size = itemSortingInfos.size();
        for (int slot = 0; slot < size; slot++) {
            items.put(slot, itemSortingInfos.get(slot).getItem());
        }
    }

    @Override protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 5), this.minigameItem.getItem(this.user.getUser()));
        super.insertFallbackItems();
    }

    protected static class ItemSortingInfo implements Comparable<ItemSortingInfo> {

        private ItemStack item;
        private MinigameServerSortingInfo minigame;

        public ItemSortingInfo(ItemStack item, int onPlayers, int maxPlayers, GameState state) {
            super();
            this.item = item;
            this.minigame = new MinigameServerSortingInfo(onPlayers, maxPlayers, state);
        }

        @Override public int compareTo(@NotNull ItemSortingInfo other) {
            int amt = minigame.compareTo(other.minigame);
            if (amt == 0) {
                amt = this.getDisplay().orElse("").compareTo(other.getDisplay().orElse(""));
            }
            return amt;
        }

        private Optional<String> getDisplay() {
            if (this.item.hasItemMeta()) {
                return Optional.ofNullable(this.item.getItemMeta().getDisplayName());
            }
            return Optional.empty();
        }

        public ItemStack getItem() {
            return ItemBuilder.item(this.item).build();
        }

    }

    public class Listener {

        @EventListener public void handle(CloudServiceUpdateEvent event) {
            MinigameInventory.this.recalculate();
        }
    }
}
