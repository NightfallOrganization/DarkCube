package eu.darkcube.system.pserver.common.packet;

public class PacketException extends RuntimeException {

	private static final long serialVersionUID = -8183059478661095719L;

	private Packet packet;

	public PacketException(Packet packet) {
		this.packet = packet;
	}

	@Override
	public String getMessage() {
		return packet.getClass().getName();
	}

	public Packet getPacket() {
		return packet;
	}
}
