package eu.darkcube.system.pserver.common.packet.packets;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperDataUpdate extends Packet {
	private UniqueId pserverId;
	private JsonDocument data;

	public PacketNodeWrapperDataUpdate(UniqueId pserverId, JsonDocument data) {
		this.pserverId = pserverId;
		this.data = data;
	}

	public JsonDocument getData() {
		return this.data;
	}

	public UniqueId getPServerId() {
		return this.pserverId;
	}
}
