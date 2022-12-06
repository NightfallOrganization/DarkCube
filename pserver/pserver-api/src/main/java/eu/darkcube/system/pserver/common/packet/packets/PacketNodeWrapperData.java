package eu.darkcube.system.pserver.common.packet.packets;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketNodeWrapperData extends Packet {

	private JsonDocument data;

	public PacketNodeWrapperData(JsonDocument data) {
		this.data = data;
	}

	public JsonDocument getData() {
		return this.data;
	}
}
