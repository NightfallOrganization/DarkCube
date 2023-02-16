/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.pserver.bukkit.event.PServerAddEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.PServerSerializable;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.packets.*;
import eu.darkcube.system.pserver.common.packets.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class WrapperPServerProvider extends PServerProvider {

	private static WrapperPServerProvider instance = new WrapperPServerProvider();

	private final Map<UniqueId, WrapperPServerExecutor> pservers = new ConcurrentHashMap<>();

	WrapperPServerExecutor pserver;

	private WrapperPServerProvider() {
		PacketNodeWrapperPServers pservers =
				new PacketWrapperNodeRetrievePServers().sendQuery(PacketNodeWrapperPServers.class);
		for (PServerSerializable s : pservers) {
			updateAndInsertIfNecessary(s);
		}
	}

	public static WrapperPServerProvider getInstance() {
		return instance;
	}

	public static void init() {

	}

	public synchronized WrapperPServerExecutor remove(UniqueId id) {
		if (!pservers.containsKey(id)) {
			return null;
		}
		WrapperPServerExecutor ps = pservers.get(id);
		try {
			publishUpdate(ps);
			publishRemove(ps);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		pservers.remove(id);
		return ps;
	}

	public void update(PServerSerializable pserver) {
		getPServerOptional(pserver.id).ifPresent(ps -> {
			ps.update(pserver);
			publishUpdate(ps);
		});
	}

	public void update(UniqueId pserverId, JsonDocument data) {
		getPServerOptional(pserverId).ifPresent(ps -> {
			ps.update(data);
			publishUpdate(ps);
		});
	}

	public synchronized WrapperPServerExecutor updateAndInsertIfNecessary(
			PServerSerializable pserver) {
		UniqueId uuid = pserver.id;
		Optional<WrapperPServerExecutor> pso = getPServerOptional(uuid);
		if (!pso.isPresent()) {
			Collection<UUID> owners = getOwners(uuid);
			WrapperPServerExecutor ps = new WrapperPServerExecutor(pserver,
					PServerProvider.getInstance().getPServerData(uuid), owners);
			pservers.put(uuid, ps);
			pso = Optional.of(ps);
			publishAdd(ps);
		} else {
			pso.get().update(pserver);
		}
		publishUpdate(pso.get());
		return getPServer(uuid);
	}

	private void publishEvent(PServerEvent event) {
		CloudNetDriver.getInstance().getEventManager().callEvent(event);
	}

	private void publishAdd(WrapperPServerExecutor ps) {
		publishEvent(new PServerAddEvent(ps));
	}

	private void publishUpdate(WrapperPServerExecutor ps) {
		publishEvent(new PServerUpdateEvent(ps));
	}

	private void publishRemove(WrapperPServerExecutor ps) {
		publishEvent(new PServerRemoveEvent(ps));
	}

	@Override
	public void setPServerCommand(BiFunction<Object, String[], Boolean> command)
	throws IllegalStateException {
		PServerWrapper.setPServerCommand(command::apply);
	}

	@Override
	public synchronized boolean isPServer() {
		return pserver != null;
	}

	@Override
	public WrapperPServerExecutor createPServer(PServerSerializable configuration) {
		return updateAndInsertIfNecessary(
				new PacketWrapperNodeCreatePServer(configuration).sendQuery()
						.cast(PacketNodeWrapperPServer.class).getInfo());
	}

	@Override
	public synchronized Collection<WrapperPServerExecutor> getPServers() {
		return pservers.values();
	}

	//	@Override
	//	public WrapperPServer createPServer(PServerSerializable configuration, ServiceTask task) {
	//		return updateAndInsertIfNecessary(new PacketWrapperNodeCreatePServer(configuration,
	//				task == null ? null : task.getName()).sendQuery()
	//				.cast(PacketNodeWrapperPServer.class).getInfo());
	//	}

	@Override
	public Collection<UniqueId> getPServerIDs(UUID owner) {
		return new PacketWrapperNodeGetPServersOfPlayer(owner).sendQuery()
				.cast(PacketNodeWrapperPServerIDs.class).getIds();
	}

	@Override
	public Collection<UUID> getOwners(UniqueId pserver) {
		return new PacketWrapperNodeGetOwners(pserver).sendQuery()
				.cast(PacketNodeWrapperOwners.class).getUuids();
	}

	@Override
	public ITask<Void> delete(UniqueId pserver) {
		return new PacketWrapperNodeDelete(pserver).sendQueryAsync().map(p -> null);
	}

	@Override
	public ITask<Void> clearOwners(UniqueId id) {
		return new PacketWrapperNodeClearOwners(id).sendQueryAsync().map(p -> null);
	}

	@Override
	public ITask<Void> addOwner(UniqueId id, UUID owner) {
		getPServerOptional(id).ifPresent(ps -> {
			ps.getOwners().add(owner);
		});
		return new PacketWrapperNodeAddOwner(id, owner).sendQueryAsync().map(p -> null);
	}

	@Override
	public ITask<Void> removeOwner(UniqueId id, UUID owner) {
		getPServerOptional(id).ifPresent(ps -> {
			ps.getOwners().remove(owner);
		});
		return new PacketWrapperNodeRemoveOwner(id, owner).sendQueryAsync().map(p -> null);
	}

	@Override
	public String newName() {
		return new PacketWrapperNodeNewName().sendQuery().cast(PacketNodeWrapperString.class)
				.getString();
	}

	@Override
	public synchronized WrapperPServerExecutor getCurrentPServer() throws IllegalStateException {
		if (pserver == null)
			throw new IllegalStateException();
		return pserver;
	}

	@Override
	public synchronized WrapperPServerExecutor getPServer(UniqueId uuid) {
		return pservers.getOrDefault(uuid, null);
	}

	@Override
	public synchronized JsonDocument getPServerData(UniqueId pserver) {
		if (pservers.containsKey(pserver)) {
			return pservers.get(pserver).getData().clone();
		}
		return new PacketWrapperNodeGetData(pserver).sendQuery().cast(PacketNodeWrapperData.class)
				.getData();
	}

	@Override
	public synchronized ITask<Void> setPServerData(UniqueId pserver, JsonDocument data) {
		if (pservers.containsKey(pserver)) {
			pservers.get(pserver).update(data);
		}
		return new PacketWrapperNodeSetData(pserver, data).sendQueryAsync().map(p -> null);
	}

	@Override
	public Optional<WrapperPServerExecutor> getPServerOptional(UniqueId uuid) {
		return Optional.ofNullable(getPServer(uuid));
	}
}
