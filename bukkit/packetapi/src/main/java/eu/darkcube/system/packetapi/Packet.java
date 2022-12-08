/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.packetapi;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;

public abstract class Packet {

	public final void send() {
		PacketAPI.getInstance().sendPacket(this);
	}

	public final void sendAsync() {
		PacketAPI.getInstance().sendPacketAsync(this);
	}

	public final void send(ServiceInfoSnapshot snapshot) {
		PacketAPI.getInstance().sendPacket(this, snapshot);
	}

	public final void sendAsync(ServiceInfoSnapshot snapshot) {
		PacketAPI.getInstance().sendPacketAsync(this, snapshot);
	}

	public final Packet sendQuery(ServiceInfoSnapshot snapshot) {
		return PacketAPI.getInstance().sendPacketQuery(this, snapshot);
	}

	public final ITask<Packet> sendQueryAsync(ServiceInfoSnapshot snapshot) {
		return PacketAPI.getInstance().sendPacketQueryAsync(this, snapshot);
	}

	public final Packet sendQuery() {
		return PacketAPI.getInstance().sendPacketQuery(this);
	}

	public final ITask<Packet> sendQueryAsync() {
		return PacketAPI.getInstance().sendPacketQueryAsync(this);
	}

	public final <T extends Packet> T sendQuery(Class<T> responseClass) {
		return PacketAPI.getInstance().sendPacketQuery(this, responseClass);
	}

	public final <T extends Packet> ITask<T> sendQueryAsync(Class<T> responseClass) {
		return PacketAPI.getInstance().sendPacketQueryAsync(this, responseClass);
	}

	public final <T extends Packet> T cast(Class<T> clazz) {
		return clazz.cast(this);
	}

	public final <T extends Packet> boolean instanceOf(Class<T> clazz) {
		return clazz.isInstance(this);
	}
}
