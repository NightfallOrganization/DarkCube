/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.packetapi;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;

public class PacketAPI {

	public static final String CHANNEL = "darkCubePacketAPI";

	private Listener listener;
	private BiMap<Class<? extends Packet>, PacketHandler<?>> handlers = HashBiMap.create();

	private PacketAPI() {
		this.listener = new Listener();
	}

	void load() {
		CloudNetDriver.getInstance().getEventManager().registerListener(listener);
	}

	void unload() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(listener);
	}

	public void sendPacket(Packet packet) {
		preparePacket(packet).sendSingleQuery();
	}

	public void sendPacketAsync(Packet packet) {
		preparePacket(packet).send();
	}

	public void sendPacket(Packet packet, ServiceInfoSnapshot snapshot) {
		preparePacket(packet, snapshot).sendSingleQuery();
	}

	public void sendPacketAsync(Packet packet, ServiceInfoSnapshot snapshot) {
		preparePacket(packet, snapshot).send();
	}

	public Packet sendPacketQuery(Packet packet, ServiceInfoSnapshot snapshot) {
		ChannelMessage msg = preparePacket(packet, snapshot).sendSingleQuery();
		return PacketSerializer.getPacket(msg.getJson(), PacketSerializer.getClass(msg.getJson()));
	}

	public ITask<Packet> sendPacketQueryAsync(Packet packet, ServiceInfoSnapshot snapshot) {
		return preparePacket(packet, snapshot).sendSingleQueryAsync().map(m -> PacketSerializer
				.getPacket(m.getJson(), PacketSerializer.getClass(m.getJson())));
	}

	public ITask<Packet> sendPacketQueryAsync(Packet packet) {
		return preparePacket(packet).sendSingleQueryAsync().map(m -> PacketSerializer
				.getPacket(m.getJson(), PacketSerializer.getClass(m.getJson())));
	}

	public Packet sendPacketQuery(Packet packet) {
		ChannelMessage msg = preparePacket(packet).sendSingleQuery();
		return PacketSerializer.getPacket(msg.getJson(), PacketSerializer.getClass(msg.getJson()));
	}

	public <T extends Packet> T sendPacketQuery(Packet packet, Class<T> responsePacketType) {
		ChannelMessage msg = preparePacket(packet).sendSingleQuery();
		return PacketSerializer.getPacket(msg.getJson(), responsePacketType);
	}

	public <T extends Packet> ITask<T> sendPacketQueryAsync(Packet packet,
			Class<T> responsePacketType) {
		return preparePacket(packet).sendSingleQueryAsync()
				.map(m -> PacketSerializer.getPacket(m.getJson(), responsePacketType));
	}

	private JsonDocument prepareDocument(Packet packet) {
		return PacketSerializer.insert(JsonDocument.newDocument(), packet);
	}

	private ChannelMessage preparePacket(Packet packet, ServiceInfoSnapshot snapshot) {
		return ChannelMessage.builder().targetService(snapshot.getName()).channel(CHANNEL)
				.json(prepareDocument(packet)).build();
	}

	private ChannelMessage preparePacket(Packet packet) {
		return ChannelMessage.builder().targetAll().channel(CHANNEL).json(prepareDocument(packet))
				.build();
	}

	public JsonDocument receive(JsonDocument doc) throws Exception {
		Class<? extends Packet> packetClass = PacketSerializer.getClass(doc);
		if (handlers.containsKey(packetClass)) {
			try {
				Packet packet = PacketSerializer.getPacket(doc, packetClass);
				@SuppressWarnings("unchecked")
				PacketHandler<Packet> handler = (PacketHandler<Packet>) handlers.get(packetClass);
				Packet response = handler.handle(packet);
				return PacketSerializer.insert(JsonDocument.newDocument(), response);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public <T extends Packet> void registerHandler(Class<T> clazz, PacketHandler<T> handler) {
		handlers.put(clazz, handler);
	}

	public void unregisterHandler(PacketHandler<?> handler) {
		handlers.inverse().remove(handler);
	}

	public class Listener {
		@EventListener
		public void handle(ChannelMessageReceiveEvent e) {
			if (e.getChannel() == null || !e.getChannel().equals(CHANNEL)) {
				return;
			}
			try {
				JsonDocument doc = receive(e.getData());
				if (e.isQuery()) {
					if (doc != null) {
						e.setQueryResponse(ChannelMessage.buildResponseFor(e.getChannelMessage())
								.json(doc).build());
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static PacketAPI instance;

	static {
		instance = new PacketAPI();
	}

	public static PacketAPI getInstance() {
		return instance;
	}

	public static void init() {

	}
}
