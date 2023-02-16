/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.util;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.pserver.bukkit.event.PServerAddEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerAddOwnerEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveOwnerEvent;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.util.AsyncExecutor;
import eu.darkcube.system.util.ReflectionUtils;
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
import java.util.stream.Collectors;

public class SkullCache implements Listener {

	private static final Method asNMSCopy = ReflectionUtils.getMethod(
			ReflectionUtils.getClass("CraftItemStack",
					ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY), "asNMSCopy",
			ItemStack.class);
	private static final Method asBukkitCopy = ReflectionUtils.getMethod(
			ReflectionUtils.getClass("CraftItemStack",
					ReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY), "asBukkitCopy",
			asNMSCopy.getReturnType());
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
		return (ItemStack) ReflectionUtils.invokeMethod(null, asBukkitCopy,
				SkullCache.cache.get(ownerUUID));
	}

	public static void register() {
		CloudNetDriver.getInstance().getEventManager().registerListener(SkullCache.sc);
		Bukkit.getPluginManager().registerEvents(SkullCache.sc, Lobby.getInstance());
		SkullCache.cache.clear();
		AsyncExecutor.service().submit(() -> {
			IPlayerManager pm = CloudNetDriver.getInstance().getServicesRegistry()
					.getFirstService(IPlayerManager.class);
			Map<UUID, String> players = new HashMap<>();
			PServerProvider.getInstance().getPServers().forEach(ps -> {
				ps.getOwners().forEach(owner -> {
					// loadToCache(owner, pm.getOfflinePlayer(owner).getName());
					players.put(owner, pm.getOfflinePlayer(owner).getName());
				});
			});
			Bukkit.getOnlinePlayers().forEach(p -> {
				if (!players.containsKey(p.getUniqueId())) {
					players.put(p.getUniqueId(), p.getName());
				}
			});
			players.entrySet().forEach(e -> {
				SkullCache.loadToCache(e.getKey(), e.getValue());
			});
		});
	}

	public static void unregister() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(SkullCache.sc);
		HandlerList.unregisterAll(SkullCache.sc);
	}

	@EventHandler
	public void handle(PlayerJoinEvent e) {
		AsyncExecutor.service().submit(() -> {
			SkullCache.loadToCache(e.getPlayer().getUniqueId(), e.getPlayer().getName());
		});
	}

	@EventHandler
	public void handle(PlayerQuitEvent e) {
		AsyncExecutor.service().submit(() -> {
			Set<UUID> uuids = new HashSet<>();
			PServerProvider.getInstance().getPServers().stream().map(PServerExecutor::getOwners)
					.forEach(uuids::addAll);
			uuids.addAll(Bukkit.getOnlinePlayers().stream().filter(s -> !s.equals(e.getPlayer()))
					.map(Player::getUniqueId).collect(Collectors.toList()));
			if (!uuids.contains(e.getPlayer().getUniqueId())) {
				SkullCache.unloadFromCache(e.getPlayer().getUniqueId());
			}
		});
	}

	@EventListener
	public void handle(PServerAddEvent e) {
		AsyncExecutor.service().submit(() -> {
			IPlayerManager pm = CloudNetDriver.getInstance().getServicesRegistry()
					.getFirstService(IPlayerManager.class);
			for (UUID owner : e.getPServer().getOwners()) {
				SkullCache.loadToCache(owner, pm.getOfflinePlayer(owner).getName());
			}
		});
	}

	@EventListener
	public void handle(PServerRemoveEvent e) {
		AsyncExecutor.service().submit(() -> {
			Set<UUID> uuids = new HashSet<>();
			PServerProvider.getInstance().getPServers().stream().filter(ps -> ps != e.getPServer())
					.map(PServerExecutor::getOwners).forEach(uuids::addAll);
			uuids.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId)
					.collect(Collectors.toList()));
			for (UUID owner : e.getPServer().getOwners()) {
				if (!uuids.contains(owner)) {
					SkullCache.unloadFromCache(owner);
				}
			}
		});
	}

	@EventListener
	public void handle(PServerAddOwnerEvent e) {
		AsyncExecutor.service().submit(() -> {
			ICloudOfflinePlayer offp = CloudNetDriver.getInstance().getServicesRegistry()
					.getFirstService(IPlayerManager.class).getOfflinePlayer(e.getOwner());
			String name =
					offp != null ? offp.getName() : Bukkit.getOfflinePlayer(e.getOwner()).getName();
			SkullCache.loadToCache(e.getOwner(), name);
		});
	}

	@EventListener
	public void handle(PServerRemoveOwnerEvent e) {
		AsyncExecutor.service().submit(() -> {
			Set<UUID> uuids = new HashSet<>();
			PServerProvider.getInstance().getPServers().stream().filter(ps -> ps != e.getPServer())
					.map(PServerExecutor::getOwners).forEach(uuids::addAll);
			uuids.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId)
					.collect(Collectors.toList()));
			if (!uuids.contains(e.getOwner())) {
				SkullCache.unloadFromCache(e.getOwner());
			}
		});
	}
}
