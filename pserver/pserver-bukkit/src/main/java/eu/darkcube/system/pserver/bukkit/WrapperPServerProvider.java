/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import de.dytanic.cloudnet.common.concurrent.ITask;
import org.bukkit.command.CommandSender;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.pserver.bukkit.command.PServerCommand;
import eu.darkcube.system.pserver.bukkit.event.PServerAddEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperData;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServerIDs;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServers;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperString;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeAddOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeClearOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeCreatePServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeDelete;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetData;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetPServersOfPlayer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeNewName;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRemoveOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRetrievePServers;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeSetData;

public class WrapperPServerProvider extends PServerProvider {

	private static WrapperPServerProvider instance = new WrapperPServerProvider();

	private final Map<UniqueId, WrapperPServer> pservers = new ConcurrentHashMap<>();

	WrapperPServer pserver;

	private WrapperPServerProvider() {
		PacketNodeWrapperPServers pservers =
				new PacketWrapperNodeRetrievePServers().sendQuery(PacketNodeWrapperPServers.class);
		for (PServerSerializable s : pservers) {
			updateAndInsertIfNecessary(s);
		}
	}

	@Override
	public synchronized ITask<Void> setPServerData(UniqueId pserver, JsonDocument data) {
		if (pservers.containsKey(pserver)) {
			pservers.get(pserver).update(data);
		}
		return new PacketWrapperNodeSetData(pserver, data).sendQueryAsync().map(p -> null);
	}

	@Override
	public synchronized JsonDocument getPServerData(UniqueId pserver) {
		if (pservers.containsKey(pserver)) {
			return pservers.get(pserver).getData();
		}
		return new PacketWrapperNodeGetData(pserver).sendQuery().cast(PacketNodeWrapperData.class)
				.getData();
	}

	@Override
	public synchronized boolean isPServer() {
		return pserver != null;
	}

	@Override
	public synchronized WrapperPServer getCurrentPServer() throws IllegalStateException {
		if (pserver == null)
			throw new IllegalStateException();
		return pserver;
	}

	@Override
	public ITask<Void> clearOwners(UniqueId id) {
		return new PacketWrapperNodeClearOwners(id).sendQueryAsync().map(p -> null);
	}

	@Override
	public Collection<UniqueId> getPServerIDs(UUID owner) {
		return new PacketWrapperNodeGetPServersOfPlayer(owner).sendQuery()
				.cast(PacketNodeWrapperPServerIDs.class).getIds();
	}

	@Override
	public ITask<Void> delete(UniqueId pserver) {
		return new PacketWrapperNodeDelete(pserver).sendQueryAsync().map(p -> null);
	}

	@Override
	public String newName() {
		return new PacketWrapperNodeNewName().sendQuery().cast(PacketNodeWrapperString.class)
				.getString();
	}

	public synchronized WrapperPServer remove(UniqueId id) {
		if (!pservers.containsKey(id)) {
			return null;
		}
		WrapperPServer ps = pservers.get(id);
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
			publishUpdate(getPServer(pserver.id));
		});
	}

	public synchronized WrapperPServer updateAndInsertIfNecessary(PServerSerializable pserver) {
		UniqueId uuid = pserver.id;
		Optional<WrapperPServer> pso = getPServerOptional(uuid);
		if (!pso.isPresent()) {
			Collection<UUID> owners = getOwners(uuid);
			WrapperPServer ps =
					new WrapperPServer(pserver, PServerProvider.getInstance().getPServerData(uuid),
							owners);
			pservers.put(uuid, ps);
			publishAdd(ps);
		} else {
			pso.get().update(pserver);
		}
		publishUpdate(getPServer(uuid));
		return getPServer(uuid);
	}

	@Override
	public Collection<UUID> getOwners(UniqueId pserver) {
		return new PacketWrapperNodeGetOwners(pserver).sendQuery()
				.cast(PacketNodeWrapperOwners.class).getUuids();
	}

	@Override
	public WrapperPServer createPServer(PServerSerializable configuration) {
		return updateAndInsertIfNecessary(
				new PacketWrapperNodeCreatePServer(configuration).sendQuery()
						.cast(PacketNodeWrapperPServer.class).getInfo());
	}

	//	@Override
	//	public WrapperPServer createPServer(PServerSerializable configuration, ServiceTask task) {
	//		return updateAndInsertIfNecessary(new PacketWrapperNodeCreatePServer(configuration,
	//				task == null ? null : task.getName()).sendQuery()
	//				.cast(PacketNodeWrapperPServer.class).getInfo());
	//	}

	@Override
	public synchronized WrapperPServer getPServer(UniqueId uuid) {
		return pservers.getOrDefault(uuid, null);
	}

	@Override
	public synchronized Collection<WrapperPServer> getPServers() {
		return pservers.values();
	}

	private void publishEvent(PServerEvent event) {
		CloudNetDriver.getInstance().getEventManager().callEvent(event);
	}

	private void publishAdd(WrapperPServer ps) {
		publishEvent(new PServerAddEvent(ps));
	}

	private void publishUpdate(WrapperPServer ps) {
		publishEvent(new PServerUpdateEvent(ps));
	}

	private void publishRemove(WrapperPServer ps) {
		publishEvent(new PServerRemoveEvent(ps));
	}

	@Override
	public Optional<WrapperPServer> getPServerOptional(UniqueId uuid) {
		return Optional.ofNullable(getPServer(uuid));
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

	public static WrapperPServerProvider getInstance() {
		return instance;
	}

	public static void init() {

	}

	@Override
	public void setPServerCommand(BiFunction<Object, String[], Boolean> command)
			throws IllegalStateException {
		PServerWrapper.setPServerCommand(new PServerCommand() {
			@Override
			public boolean execute(CommandSender sender, String[] args) {
				return command.apply(sender, args);
			}
		});
	}
}
