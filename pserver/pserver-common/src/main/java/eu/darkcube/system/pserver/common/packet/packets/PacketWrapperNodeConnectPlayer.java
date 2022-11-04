package eu.darkcube.system.pserver.common.packet.packets;

import java.util.UUID;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeConnectPlayer extends Packet {

	private UUID player;
	private UniqueId pserver;

	public PacketWrapperNodeConnectPlayer(UUID player, UniqueId pserver) {
		this.player = player;
		this.pserver = pserver;
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	public UniqueId getPServer() {
		return pserver;
	}
}
