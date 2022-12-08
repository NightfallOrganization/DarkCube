/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;

public abstract class Packet {

	public final void send() {
		PacketManager.getInstance().sendPacket(this);
	}

	public final void sendAsync() {
		PacketManager.getInstance().sendPacketAsync(this);
	}

	public final void send(ServiceInfoSnapshot snapshot) {
		PacketManager.getInstance().sendPacket(this, snapshot);
	}

	public final void sendAsync(ServiceInfoSnapshot snapshot) {
		PacketManager.getInstance().sendPacketAsync(this, snapshot);
	}

	public final Packet sendQuery(ServiceInfoSnapshot snapshot) {
		return PacketManager.getInstance().sendPacketQuery(this, snapshot);
	}

	public final ITask<Packet> sendQueryAsync(ServiceInfoSnapshot snapshot) {
		return PacketManager.getInstance().sendPacketQueryAsync(this, snapshot);
	}

	public final Packet sendQuery() {
		return PacketManager.getInstance().sendPacketQuery(this);
	}

	public final ITask<Packet> sendQueryAsync() {
		return PacketManager.getInstance().sendPacketQueryAsync(this);
	}

	public final <T extends Packet> T sendQuery(Class<T> responseClass) {
		return PacketManager.getInstance().sendPacketQuery(this, responseClass);
	}

	public final <T extends Packet> ITask<T> sendQueryAsync(Class<T> responseClass) {
		return PacketManager.getInstance().sendPacketQueryAsync(this, responseClass);
	}

	public final <T extends Packet> T cast(Class<T> clazz) {
		return clazz.cast(this);
	}

	public final <T extends Packet> boolean instanceOf(Class<T> clazz) {
		return clazz.isInstance(this);
	}
}
