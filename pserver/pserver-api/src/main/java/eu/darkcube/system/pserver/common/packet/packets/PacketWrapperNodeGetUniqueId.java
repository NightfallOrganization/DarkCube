/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
