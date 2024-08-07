/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection;
import eu.darkcube.system.lobbysystem.listener.ListenerPServer;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.PServerUtil;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.pserver.bukkit.event.PServerStartEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerStopEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.State;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

public class InventoryPServer extends LobbyAsyncPagedInventory {

    public static final String ITEMID = "lobbysystem.pserver.publiclist";

    public static final Key META_KEY_PSERVER = Key.key(Lobby.getInstance(), "lobbysystem.pserver.id");

    private static final InventoryType type_pserver = InventoryType.of("pserver");

    private Listener listener;

    public InventoryPServer(LobbyUser user) {
        super(InventoryPServer.type_pserver, Item.PSERVER_PUBLIC.getDisplayName(user.user()), user);
        // super(Item.PSERVER_MAIN_ITEM.getDisplayName(user), InventoryPServer.type_pserver);
        this.listener = new Listener();
        this.listener.register();
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        var itemid = Item.getItemId(event.item());
        if (itemid == null) return;
        if (!event.item().persistentDataStorage().has(META_KEY_PSERVER)) return;
        var id = new UniqueId(event.item().persistentDataStorage().get(META_KEY_PSERVER, PersistentDataTypes.STRING));
        var user = UserWrapper.fromUser(event.user());
        var queryFuture = PServerProvider.instance().pserver(id);
        user.player().orElseThrow().closeInventory();
        queryFuture.thenCompose(ps -> PServerUtil.mayJoin(user, ps)).thenAcceptBoth(queryFuture, (suc, executor) -> {
            if (!suc) {
                user.user().sendMessage(Message.PSERVER_NOT_PUBLIC);
                return;
            }
            var l = InjectionLayer.ext().instance(ListenerPServer.class);
            if (!l.setStarted(event.user(), executor.id(), fail -> {
            })) return;
            l.connectPlayer(user.user(), executor);
        });
    }

    @Override
    protected void fillItems(Map<Integer, ItemStack> items) {
        SortedMap<Long, ItemStack> sitems = new TreeMap<>();

        for (PServerExecutor ps : PServerProvider.instance().pservers().join()) {
            State state = ps.state().join();
            if (state != State.RUNNING) continue;
            if (!PServerUtil.mayJoin(user, ps).join()) continue;
            AccessLevel accessLevel = ps.accessLevel().join();
            boolean publicServer = accessLevel == AccessLevel.PUBLIC;
            int online = ps.onlinePlayers().join();
            long ontime = ps.ontime().join();
            UUID owner = ps.owners().join().stream().findAny().orElse(null);

            ItemBuilder b = null;

            if (ps.type().join() == Type.GAMEMODE) {
                var entry = ps.storage().get(InventoryGameServerSelection.SERVICE, InventoryGameServerSelection.SERVICE_TYPE);
                b = PServerDataManager.getDisplayItemGamemode(this.user.user(), entry);
            }
            if (b == null) {
                if (owner == null) {
                    b = ItemBuilder.item(Material.BARRIER);
                } else {
                    b = ItemBuilder.item(Material.SKULL_ITEM).damage(SkullType.PLAYER.ordinal());
                    ItemStack item = b.build();
                    item.setItemMeta(SkullCache.getCachedItem(owner).getItemMeta());
                    b = ItemBuilder.item(item);
                }
            }
            b.amount(online);
            b.displayname(Message.PSERVER_ITEM_TITLE.getMessage(this.user.user(), ps.serverName().join()));
            b.lore(publicServer ? Message.CLICK_TO_JOIN.getMessage(this.user.user()) : Message.PSERVER_NOT_PUBLIC.getMessage(this.user.user()));
            Item.setItemId(b, InventoryPServer.ITEMID);
            b.persistentDataStorage().set(InventoryPServer.META_KEY_PSERVER, PersistentDataTypes.STRING, ps.id().toString());
            sitems.put(ontime, b.build());
        }

        int slot = 0;
        for (long ontime : sitems.keySet()) {
            items.put(slot, sitems.get(ontime));
            slot++;
        }
    }

    @Override
    protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 3), Item.LIME_GLASS_PANE.getItem(this.user.user()));
        this.fallbackItems.put(IInventory.slot(1, 4), Item.INVENTORY_PSERVER_PUBLIC.getItem(this.user.user()));
        this.fallbackItems.put(IInventory.slot(1, 5), Item.LIME_GLASS_PANE.getItem(this.user.user()));
        this.fallbackItems.put(IInventory.slot(1, 6), Item.INVENTORY_PSERVER_PRIVATE.getItem(this.user.user()));
        super.insertFallbackItems();
    }

    @Override
    protected void destroy() {
        super.destroy();
        this.listener.unregister();
    }

    public class Listener {

        public void register() {
            InjectionLayer.boot().instance(EventManager.class).registerListener(this);
        }

        public void unregister() {
            InjectionLayer.boot().instance(EventManager.class).unregisterListener(this);
        }

        @EventListener
        public void handle(PServerUpdateEvent event) {
            InventoryPServer.this.recalculate();
        }

        @EventListener
        public void handle(PServerStartEvent event) {
            recalculate();
        }

        @EventListener
        public void handle(PServerStopEvent event) {
            recalculate();
        }

    }

}
