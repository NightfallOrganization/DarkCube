/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.userapi;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.ext.bridge.event.BridgeProxyPlayerDisconnectEvent;
import de.dytanic.cloudnet.ext.bridge.event.BridgeProxyPlayerLoginSuccessEvent;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.packets.PacketQueryUser;
import eu.darkcube.system.userapi.packets.PacketQueryUser.Result;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataSet;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataUpdate;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class UserAPI {

	private final ConcurrentHashMap<UUID, ModuleUser> users = new ConcurrentHashMap<>();
	private final Database database =
			CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("userapi_users");
	private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry()
			.getFirstService(IPlayerManager.class);
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

	public UserAPI() {
		PacketAPI.getInstance().registerHandler(PacketUserPersistentDataSet.class, packet -> {
			modifyUser(packet.getUniqueId(), user -> {
				user.getStorage().data.append(packet.getData());
				new PacketUserPersistentDataUpdate(user.getUniqueId(),
						user.getStorage().data.clone()).sendAsync();
			});
			return null;
		});
		PacketAPI.getInstance().registerHandler(PacketUserPersistentDataRemove.class, packet -> {
			modifyUser(packet.getUniqueId(), user -> {
				user.getStorage().data.remove(packet.getKey().toString());
				new PacketUserPersistentDataUpdate(user.getUniqueId(),
						user.getStorage().data.clone()).sendAsync();
			});
			return null;
		});
		PacketAPI.getInstance().registerHandler(PacketQueryUser.class, packet -> {
			AtomicReference<String> name = new AtomicReference<>();
			AtomicReference<JsonDocument> data = new AtomicReference<>();
			modifyUser(packet.getUniqueId(), user -> {
				name.set(user.getName());
				data.set(user.getStorage().data.clone());
			});
			return new Result(name.get(), data.get());
		});
	}

	@EventListener
	public void handle(BridgeProxyPlayerLoginSuccessEvent event) {
		loadUser(event.getNetworkConnectionInfo().getUniqueId());
	}

	@EventListener
	public void handle(BridgeProxyPlayerDisconnectEvent event) {
		unload(users.get(event.getNetworkConnectionInfo().getUniqueId()));
	}

	public void modifyUser(UUID uuid, final Consumer<ModuleUser> consumer) {
		final AtomicBoolean worked = new AtomicBoolean(false);
		lock.writeLock().lock();
		users.computeIfPresent(uuid, (uid, user) -> {
			consumer.accept(user);
			worked.set(true);
			return user;
		});
		if (!worked.get()) {
			ModuleUser user = load(uuid);
			consumer.accept(user);
			save(user, () -> {
			});
		}
		lock.writeLock().unlock();
	}

	private void loadUser(UUID uuid) {
		users.computeIfAbsent(uuid, this::load);
	}

	private void save(ModuleUser user, Runnable runnable) {
		JsonDocument entry = new JsonDocument();
		entry.append("name", user.getName());
		entry.append("uuid", user.getUniqueId().toString());
		user.getStorage().lock.readLock().lock();
		entry.append("persistentData", user.getStorage().data.clone());
		user.getStorage().lock.readLock().unlock();
		database.containsAsync(user.getUniqueId().toString()).fireExceptionOnFailure()
				.onComplete(b -> {
					if (b) {
						database.updateAsync(user.getUniqueId().toString(), entry)
								.fireExceptionOnFailure().onComplete(r -> runnable.run());
					} else {
						database.insertAsync(user.getUniqueId().toString(), entry)
								.fireExceptionOnFailure().onComplete(r -> runnable.run());
					}
				});
	}

	private void unload(ModuleUser user) {
		save(user, () -> users.remove(user.getUniqueId()));
	}

	private ModuleUser load(UUID uuid) {
		lock.writeLock().lock();
		JsonDocument entry = null;
		String name;

		if (database.contains(uuid.toString())) {
			entry = database.get(uuid.toString());
			name = entry.getString("name");
		} else {
			ICloudOfflinePlayer player = playerManager.getOfflinePlayer(uuid);
			if (player != null) {
				name = player.getName();
			} else {
				name = uuid.toString().substring(0, 16);
			}
		}

		ModuleUser user = new ModuleUser(uuid, name);
		if (entry != null) {
			user.getStorage().data.append(entry.getDocument("persistentData"));
		}
		lock.writeLock().unlock();
		return user;
	}

}
