package eu.darkcube.system.userapi.packets;

import java.util.UUID;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.userapi.data.Key;

public class PacketUserPersistentDataRemove extends Packet {
	private UUID uuid;
	private Key key;

	public PacketUserPersistentDataRemove(UUID uuid, Key key) {
		this.uuid = uuid;
		this.key = key;
	}

	public Key getKey() {
		return this.key;
	}

	public UUID getUniqueId() {
		return this.uuid;
	}
}
