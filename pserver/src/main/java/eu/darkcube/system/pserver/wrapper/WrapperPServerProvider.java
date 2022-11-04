package eu.darkcube.system.pserver.wrapper;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

import org.bukkit.command.*;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.pserver.common.*;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServerIDs;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServers;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperString;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeAddOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeClearOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeCreatePServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeDelete;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetPServersOfPlayer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeNewName;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRemoveOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRetrievePServers;
import eu.darkcube.system.pserver.wrapper.command.*;
import eu.darkcube.system.pserver.wrapper.event.PServerAddEvent;
import eu.darkcube.system.pserver.wrapper.event.PServerEvent;
import eu.darkcube.system.pserver.wrapper.event.PServerRemoveEvent;
import eu.darkcube.system.pserver.wrapper.event.PServerUpdateEvent;

public class WrapperPServerProvider extends PServerProvider {

	private static WrapperPServerProvider instance = new WrapperPServerProvider();

	private final Map<UniqueId, WrapperPServer> pservers = new ConcurrentHashMap<>();

	PServer pserver;

	private WrapperPServerProvider() {
		PacketNodeWrapperPServers pservers = new PacketWrapperNodeRetrievePServers()
				.sendQuery(PacketNodeWrapperPServers.class);
		for (PServerSerializable s : pservers) {
			updateAndInsertIfNecessary(s);
		}
	}

	@Override
	public boolean isPServer() {
		return pserver != null;
	}

	@Override
	public PServer getCurrentPServer() throws IllegalStateException {
		if (pserver == null)
			throw new IllegalStateException();
		return pserver;
	}

	@Override
	public void clearOwners(UniqueId id) {
		new PacketWrapperNodeClearOwners(id).send();
	}

	@Override
	public Collection<UniqueId> getPServerIDs(UUID owner) {
		return new PacketWrapperNodeGetPServersOfPlayer(owner).sendQuery().cast(PacketNodeWrapperPServerIDs.class)
				.getIds();
	}

	@Override
	public void delete(UniqueId pserver) {
		new PacketWrapperNodeDelete(pserver).sendAsync();
	}

	@Override
	public String newName() {
		return new PacketWrapperNodeNewName().sendQuery().cast(PacketNodeWrapperString.class).getString();
	}

	public WrapperPServer remove(UniqueId id) {
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
		UniqueId uuid = pserver.id;
		if (pservers.containsKey(uuid)) {
			getPServer(uuid).update(pserver);
			publishUpdate(getPServer(uuid));
		}
	}

	public synchronized WrapperPServer updateAndInsertIfNecessary(PServerSerializable pserver) {
		UniqueId uuid = pserver.id;
		if (!pservers.containsKey(uuid)) {
			WrapperPServer ps = new WrapperPServer(pserver);
			pservers.put(uuid, ps);
			publishAdd(ps);
		} else {
			getPServer(uuid).update(pserver);
		}
		publishUpdate(getPServer(uuid));
		return getPServer(uuid);
	}

	@Override
	public Collection<UUID> getOwners(UniqueId pserver) {
		return new PacketWrapperNodeGetOwners(pserver).sendQuery().cast(PacketNodeWrapperOwners.class).getUuids();
	}

	@Override
	public WrapperPServer createPServer(PServerSerializable configuration) {
		return createPServer(configuration, null);
	}

	@Override
	public WrapperPServer createPServer(PServerSerializable configuration, ServiceTask task) {
		return updateAndInsertIfNecessary(
				new PacketWrapperNodeCreatePServer(configuration, task == null ? null : task.getName()).sendQuery()
						.cast(PacketNodeWrapperPServer.class).getInfo());
	}

	@Override
	public WrapperPServer getPServer(UniqueId uuid) {
		return pservers.getOrDefault(uuid, null);
	}

	@Override
	public Collection<WrapperPServer> getPServers() {
		return pservers.values();
	}

	private void publishEvent(PServerEvent event) {
		CloudNetDriver.getInstance().getEventManager().callEvent(event);
	}

	private WrapperPServer publishAdd(WrapperPServer ps) {
		publishEvent(new PServerAddEvent(ps));
		return ps;
	}

	private WrapperPServer publishUpdate(WrapperPServer ps) {
		publishEvent(new PServerUpdateEvent(ps));
		return ps;
	}

	private WrapperPServer publishRemove(WrapperPServer ps) {
		publishEvent(new PServerRemoveEvent(ps));
		return ps;
	}

	@Override
	public Optional<WrapperPServer> getPServerOptional(UniqueId uuid) {
		return Optional.ofNullable(getPServer(uuid));
	}

	@Override
	public void addOwner(UniqueId id, UUID owner) {
		new PacketWrapperNodeAddOwner(id, owner).sendAsync();
		getPServerOptional(id).ifPresent(ps -> {
			ps.getOwners().add(owner);
		});
	}

	@Override
	public void removeOwner(UniqueId id, UUID owner) {
		new PacketWrapperNodeRemoveOwner(id, owner).sendAsync();
		getPServerOptional(id).ifPresent(ps -> {
			ps.getOwners().remove(owner);
		});
	}

	public static WrapperPServerProvider getInstance() {
		return instance;
	}

	public static void init() {

	}

	@Override
	public void setPServerCommand(BiFunction<Object, String[], Boolean> command) throws IllegalStateException {
		PServerWrapper.setPServerCommand(new PServerCommand() {
			@Override
			public boolean execute(CommandSender sender, String[] args) {
				return command.apply(sender, args);
			}
		});
	}
}
