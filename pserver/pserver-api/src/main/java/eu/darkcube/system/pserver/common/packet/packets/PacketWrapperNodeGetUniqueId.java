package eu.darkcube.system.pserver.common.packet.packets;

import de.dytanic.cloudnet.driver.service.ServiceId;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeGetUniqueId extends Packet {

	private ServiceId serviceId;
	
	public PacketWrapperNodeGetUniqueId(ServiceId serviceId) {
		this.serviceId = serviceId;
	}
	
	public ServiceId getServiceId() {
		return serviceId;
	}
}
