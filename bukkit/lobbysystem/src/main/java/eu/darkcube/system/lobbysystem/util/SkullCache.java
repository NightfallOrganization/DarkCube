/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.util;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.modules.bridge.player.CloudOfflinePlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.darkcube.system.bukkit.util.ReflectionUtils;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.pserver.bukkit.event.PServerAddOwnerEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveOwnerEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerStartEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerStopEvent;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.util.AsyncExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SkullCache implements Listener {

    private static final Method asNMSCopy = ReflectionUtils.getMethod(ReflectionUtils.getClass("CraftItemStack", ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY), "asNMSCopy", ItemStack.class);
    private static final Method asBukkitCopy = ReflectionUtils.getMethod(ReflectionUtils.getClass("CraftItemStack", ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY), "asBukkitCopy", asNMSCopy.getReturnType());
    public static Map<UUID, Object> cache = new HashMap<>();
    private static SkullCache sc = new SkullCache();

    public static void loadToCache(UUID ownerUUID, String ownerName) {
        if (SkullCache.cache.containsKey(ownerUUID)) {
            return;
        }
        ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) i.getItemMeta();
        meta.setOwner(ownerName);
        meta.setDisplayName(ownerName);
        i.setItemMeta(meta);
        SkullCache.cache.put(ownerUUID, ReflectionUtils.invokeMethod(null, asNMSCopy, i));
    }

    public static void unloadFromCache(UUID ownerUUID) {
        SkullCache.cache.remove(ownerUUID);
    }

    public static ItemStack getCachedItem(UUID ownerUUID) {
        return (ItemStack) ReflectionUtils.invokeMethod(null, asBukkitCopy, SkullCache.cache.get(ownerUUID));
    }

    public static void register() {
        InjectionLayer.boot().instance(EventManager.class).registerListener(SkullCache.sc);
        Bukkit.getPluginManager().registerEvents(SkullCache.sc, Lobby.getInstance());
        SkullCache.cache.clear();
        AsyncExecutor.service().submit(() -> {
            PlayerManager pm = InjectionLayer.boot().instance(PlayerManager.class);
            Map<UUID, String> players = new HashMap<>();
            try {
                for (PServerExecutor ps : PServerProvider.instance().pservers().get()) {
                    for (UUID owner : ps.owners().get()) {
                        players.put(owner, pm.offlinePlayer(owner).name());
                    }
                }
                //						.forEach(ps -> {
                //					ps.getOwners().forEach(owner -> {
                //						// loadToCache(owner, pm.getOfflinePlayer(owner).getName());
                //						players.put(owner, pm.getOfflinePlayer(owner).getName());
                //					});
                //				});
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!players.containsKey(p.getUniqueId())) {
                    players.put(p.getUniqueId(), p.getName());
                }
            });
            players.forEach(SkullCache::loadToCache);
        });
    }

    public static void unregister() {
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(SkullCache.sc);
        HandlerList.unregisterAll(SkullCache.sc);
    }

    @EventHandler public void handle(PlayerJoinEvent e) {
        AsyncExecutor.service().submit(() -> {
            SkullCache.loadToCache(e.getPlayer().getUniqueId(), e.getPlayer().getName());
        });
    }

    @EventHandler public void handle(PlayerQuitEvent e) {
        AsyncExecutor.service().submit(() -> {
            try {
                Set<UUID> uuids = new HashSet<>();
                PServerProvider.instance().pservers().get().stream().map(PServerExecutor::owners).map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                }).forEach(uuids::addAll);
                uuids.addAll(Bukkit
                        .getOnlinePlayers()
                        .stream()
                        .filter(s -> !s.equals(e.getPlayer()))
                        .map(Player::getUniqueId)
                        .collect(Collectors.toList()));
                if (!uuids.contains(e.getPlayer().getUniqueId())) {
                    SkullCache.unloadFromCache(e.getPlayer().getUniqueId());
                }
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @EventListener public void handle(PServerStartEvent e) {
        AsyncExecutor.service().submit(() -> {
            PlayerManager pm = InjectionLayer.boot().instance(PlayerManager.class);
            try {
                for (UUID owner : e.pserver().owners().get()) {
                    SkullCache.loadToCache(owner, pm.offlinePlayer(owner).name());
                }
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @EventListener public void handle(PServerStopEvent e) {
        AsyncExecutor.service().submit(() -> {
            Set<UUID> uuids = new HashSet<>();
            try {
                PServerProvider.instance().pservers().get().stream().filter(ps -> ps != e.pserver()).map(PServerExecutor::owners).map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                }).forEach(uuids::addAll);
                uuids.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
                for (UUID owner : e.pserver().owners().get()) {
                    if (!uuids.contains(owner)) {
                        SkullCache.unloadFromCache(owner);
                    }
                }
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @EventListener public void handle(PServerAddOwnerEvent e) {
        AsyncExecutor.service().submit(() -> {
            PlayerManager pm = InjectionLayer.boot().instance(PlayerManager.class);
            CloudOfflinePlayer offp = pm.offlinePlayer(e.getOwner());
            String name = offp != null ? offp.name() : Bukkit.getOfflinePlayer(e.getOwner()).getName();
            SkullCache.loadToCache(e.getOwner(), name);
        });
    }

    @EventListener public void handle(PServerRemoveOwnerEvent e) {
        AsyncExecutor.service().submit(() -> {
            Set<UUID> uuids = new HashSet<>();
            try {
                PServerProvider.instance().pservers().get().stream().filter(ps -> ps != e.pserver()).map(PServerExecutor::owners).map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                }).forEach(uuids::addAll);
                uuids.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
                if (!uuids.contains(e.getOwner())) {
                    SkullCache.unloadFromCache(e.getOwner());
                }
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
