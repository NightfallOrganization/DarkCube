package eu.darkcube.system.userapi.packets;

import java.util.UUID;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.packetapi.Packet;

public class PacketUserPersistentDataSet extends Packet {
	private UUID uuid;
	private JsonDocument data;

	public PacketUserPersistentDataSet(UUID uuid, JsonDocument data) {
		this.uuid = uuid;
		this.data = data;
	}

	public JsonDocument getData() {
		return this.data;
	}

	public UUID getUniqueId() {
		return this.uuid;
	}
}
