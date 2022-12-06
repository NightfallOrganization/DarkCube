package eu.darkcube.system.pserver.bukkit;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketException;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperBoolean;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeConnectPlayer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRemove;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeSetData;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeStart;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeStop;

public class WrapperPServer implements PServer {

	private UniqueId id;
	private State state;
	private boolean temporary;
	private long startedAt;
	private Collection<UUID> owners;
	private String serverName;
	private int online;
	private String taskName;
	private PServerSerializable serializable;
	private JsonDocument data;

	WrapperPServer(PServerSerializable info, JsonDocument data) {
		this.update(info, data);
	}

	public synchronized void update(PServerSerializable info, JsonDocument data) {
		this.serializable = info;
		this.data = data;

		this.id = info.id;
		this.state = info.state;
		this.online = info.online;
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

	@Override
	public synchronized JsonDocument getData() {
		return this.data;
	}

	@Override
	public PServerSerializable getSerializable() {
		return serializable;
	}

	@Override
	public synchronized void setPrivate(boolean privateServer) {
		data.append("private", privateServer);
		new PacketWrapperNodeSetData(id, data).sendAsync();
	}

	@Override
	public synchronized boolean isPrivate() {
		return data.getBoolean("private", false);
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
