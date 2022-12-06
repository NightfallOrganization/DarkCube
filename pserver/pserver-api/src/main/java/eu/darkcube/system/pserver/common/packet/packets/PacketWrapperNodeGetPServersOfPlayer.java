package eu.darkcube.system.pserver.common.packet.packets;

import java.util.UUID;

import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeGetPServersOfPlayer extends Packet {

	private UUID player;
	
	public PacketWrapperNodeGetPServersOfPlayer(UUID player) {
		this.player = player;
	}
	
	public UUID getPlayer() {
		return player;
	}
}
