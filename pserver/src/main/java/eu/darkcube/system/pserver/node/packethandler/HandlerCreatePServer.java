package eu.darkcube.system.pserver.node.packethandler;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.pserver.common.packet.Packet;
import eu.darkcube.system.pserver.common.packet.PacketHandler;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeCreatePServer;
import eu.darkcube.system.pserver.node.NodePServerProvider;

public class HandlerCreatePServer implements PacketHandler<PacketWrapperNodeCreatePServer> {

	@Override
	public Packet handle(PacketWrapperNodeCreatePServer packet) {
		Packet ps = new PacketNodeWrapperPServer(NodePServerProvider.getInstance()
				.createPServer(packet.getInfo(), packet.getTask() == null ? null
						: CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(packet.getTask()))
				.getSerializable());
		return ps;
	}
}
