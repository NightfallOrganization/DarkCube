package eu.darkcube.system.pserver.common.packet.packets;

import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.common.packet.Packet;

public class PacketWrapperNodeCreatePServer extends Packet {

	private PServerSerializable pserver;
	private String taskname;

	public PacketWrapperNodeCreatePServer(PServerSerializable pserver, String task) {
		this.pserver = pserver;
		this.taskname = task;
	}

	public PServerSerializable getInfo() {
		return pserver;
	}

	public String getTask() {
		return taskname;
	}
}
