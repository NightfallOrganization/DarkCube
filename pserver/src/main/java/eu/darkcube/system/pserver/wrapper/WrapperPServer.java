package eu.darkcube.system.pserver.wrapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketException;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperBoolean;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeConnectPlayer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodePrivate;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRemove;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeStart;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeStop;

public class WrapperPServer implements PServer {

	private UniqueId id;
	private State state;
	private boolean privateServer;
	private boolean temporary;
	private long startedAt;
	private Collection<UUID> owners;
	private String serverName;
	private int online;
	private String taskName;
	private PServerSerializable serializable;

	WrapperPServer(PServerSerializable info) {
		this.update(info);
	}

	void update(PServerSerializable info) {
		this.serializable = info;

		this.id = info.id;
		this.state = info.state;
		this.online = info.online;
		this.privateServer = info.privateServer;
		this.temporary = info.temporary;
		this.startedAt = info.startedAt;
		this.owners = new HashSet<>(info.owners);
		this.taskName = info.taskName;
		this.serverName = info.serverName;
	}

	@Override
	public String getTaskName() {
		return taskName;
	}

//	@Override
//	public void addOwner(UUID owner) {
//		Packet packet = new PacketWrapperNodeAddOwner(id, owner).sendQuery();
//		if (packet.instanceOf(PacketNodeWrapperActionConfirm.class)) {
//			this.owners.add(owner);
//		} else if (packet.instanceOf(PacketNodeWrapperPServerNotFound.class)) {
//			return;
//		}
//	}
//
//	@Override
//	public void removeOwner(UUID owner) {
//		Packet packet = new PacketWrapperNodeRemoveOwner(id, owner).sendQuery();
//		if (packet.instanceOf(PacketNodeWrapperActionConfirm.class)) {
//			this.owners.remove(owner);
//		} else if (packet.instanceOf(PacketNodeWrapperPServerNotFound.class)) {
//			return;
//		}
//	}

	@Override
	public PServerSerializable getSerializable() {
		return serializable;
	}
	
	@Override
	public void setPrivate(boolean privateServer) {
		this.privateServer = privateServer;
		new PacketWrapperNodePrivate(id, privateServer).sendAsync();
	}

	@Override
	public void remove() {
		new PacketWrapperNodeRemove(id).sendQuery();
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public boolean start() {
		Packet packet = new PacketWrapperNodeStart(id).sendQuery();
		if (packet.instanceOf(PacketNodeWrapperBoolean.class)) {
			return packet.cast(PacketNodeWrapperBoolean.class).is();
		}
		new PacketException(packet).printStackTrace();
		return false;
	}

	@Override
	public boolean stop() {
		Packet packet = new PacketWrapperNodeStop(id).sendQuery();
		if (packet.instanceOf(PacketNodeWrapperBoolean.class)) {
			return packet.cast(PacketNodeWrapperBoolean.class).is();
		}
		new PacketException(packet).printStackTrace();
		return false;
	}

	@Override
	public void connectPlayer(UUID player) {
		new PacketWrapperNodeConnectPlayer(player, id).sendAsync();
	}

	@Override
	public int getOnlinePlayers() {
		return online;
	}

	@Override
	public boolean isGamemode() {
		return temporary;
	}

	@Override
	public boolean isPrivate() {
		return privateServer;
	}

	@Override
	public boolean isPublic() {
		return !isPrivate();
	}

	@Override
	public UniqueId getId() {
		return id;
	}

	@Override
	public long getOntime() {
		return System.currentTimeMillis() - getStartedAt();
	}

	@Override
	public Collection<UUID> getOwners() {
		return owners;
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public long getStartedAt() {
		return startedAt;
	}
}
