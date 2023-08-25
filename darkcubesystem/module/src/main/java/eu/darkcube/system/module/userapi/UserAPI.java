/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.module.userapi;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.modules.bridge.event.BridgeProxyPlayerDisconnectEvent;
import eu.cloudnetservice.modules.bridge.event.BridgeProxyPlayerLoginEvent;
import eu.cloudnetservice.modules.bridge.player.CloudOfflinePlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.packets.*;
import eu.darkcube.system.userapi.packets.PacketQueryUser.Result;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class UserAPI {

    private final ConcurrentHashMap<UUID, ModuleUser> users = new ConcurrentHashMap<>();
    private final Database database = InjectionLayer.boot().instance(DatabaseProvider.class).database("userapi_users");
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

    public UserAPI() {
        PacketAPI.getInstance().registerHandler(PacketUserPersistentDataSet.class, packet -> {
            modifyUser(packet.getUniqueId(), user -> {
                user.getStorage().lock.writeLock().lock();
                user.getStorage().data.append(packet.getData());
                new PacketUserPersistentDataMerge(user.getUniqueId(), user.getStorage().data.immutableCopy()).sendAsync();
                user.getStorage().lock.writeLock().unlock();
            });
            return null;
        });
        PacketAPI.getInstance().registerHandler(PacketUserPersistentDataRemove.class, packet -> {
            modifyUser(packet.getUniqueId(), user -> {
                user.getStorage().data.remove(packet.getKey().toString());
                new PacketNWUserPersistentDataRemove(user.getUniqueId(), packet.getKey()).sendAsync();
            });
            return null;
        });
        PacketAPI.getInstance().registerHandler(PacketQueryUser.class, packet -> {
            AtomicReference<String> name = new AtomicReference<>();
            AtomicReference<Document> data = new AtomicReference<>();
            modifyUser(packet.getUniqueId(), user -> {
                name.set(user.getName());
                user.getStorage().lock.readLock().lock();
                data.set(user.getStorage().data.immutableCopy());
                user.getStorage().lock.readLock().unlock();
            });
            return new Result(name.get(), data.get());
        });
    }

    @EventListener public void handle(BridgeProxyPlayerLoginEvent event) {
        loadUser(event.cloudPlayer().uniqueId());
    }

    @EventListener public void handle(BridgeProxyPlayerDisconnectEvent event) {
        unload(users.get(event.cloudPlayer().uniqueId()));
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
        Document.Mutable entry = Document.newJsonDocument();
        entry.append("name", user.getName());
        entry.append("uuid", user.getUniqueId().toString());
        user.getStorage().lock.readLock().lock();
        runnable.run();
        entry.append("persistentData", user.getStorage().data.immutableCopy());
        user.getStorage().lock.readLock().unlock();
        database.insertAsync(user.getUniqueId().toString(), entry);
    }

    private void unload(ModuleUser user) {
        save(user, () -> users.remove(user.getUniqueId()));
    }

    private ModuleUser load(UUID uuid) {
        lock.writeLock().lock();
        Document entry = null;
        String name = null;
        boolean useCloud = true;

        if (database.contains(uuid.toString())) {
            entry = database.get(uuid.toString());
            name = entry.getString("name");
            if (!uuid.toString().startsWith(name)) {
                useCloud = false;
            }
        }
        if (useCloud) {
            CloudOfflinePlayer player = InjectionLayer
                    .boot()
                    .instance(ServiceRegistry.class)
                    .firstProvider(PlayerManager.class)
                    .offlinePlayer(uuid);
            if (player != null) {
                name = player.name();
            } else {
                name = uuid.toString().substring(0, 16);
            }
        }

        ModuleUser user = new ModuleUser(uuid, name);
        if (entry != null) {
            user.getStorage().data.append(entry.readDocument("persistentData"));
        }
        lock.writeLock().unlock();
        return user;
    }

}
