/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.packetapi;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import eu.cloudnetservice.common.concurrent.Task;
import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacketAPI {

    private static final String CHANNEL = "darkcube:packetapi";
    private static final String MESSAGE_PACKET = "packet";
    private static final byte TYPE_NO_RESPONSE = 0;
    private static final byte TYPE_QUERY = 1;
    private static final byte TYPE_QUERY_RESPONSE = 2;
    private static PacketAPI instance;

    static {
        instance = new PacketAPI();
    }

    private Listener listener;
    private EventManager eventManager = InjectionLayer.boot().instance(EventManager.class);
    private BiMap<Class<? extends Packet>, PacketHandler<?>> handlers = HashBiMap.create();
    private Cache<UUID, QueryEntry<? extends Packet>> queries = Caffeine
            .newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .scheduler(Scheduler.systemScheduler())
            .removalListener((UUID $, QueryEntry<? extends Packet> value, RemovalCause cause) -> {
                if (cause.wasEvicted() && value != null) {
                    value.task().completeExceptionally(new TimeoutException());
                }
            })
            .build();

    private PacketAPI() {
        this.listener = new Listener();
        load();
    }

    public static PacketAPI getInstance() {
        return instance;
    }

    public static void init() {
    }

    void load() {
        eventManager.registerListener(listener);
    }

    void unload() {
        eventManager.unregisterListener(listener);
    }

    public void sendPacket(Packet packet) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetAll().build().send();
    }

    public void sendPacketAsync(Packet packet) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetAll().build().send();
    }

    public void sendPacket(Packet packet, ServiceInfoSnapshot snapshot) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetService(snapshot.name()).build().send();
    }

    public void sendPacketAsync(Packet packet, ServiceInfoSnapshot snapshot) {
        preparePacket(packet, null, TYPE_NO_RESPONSE).targetService(snapshot.name()).build().send();
    }

    public <T extends Packet> T sendPacketQuery(Packet packet, Class<T> responsePacketType) {
        return sendPacketQueryAsync(packet, responsePacketType).join();
    }

    public <T extends Packet> Task<T> sendPacketQueryAsync(Packet packet, Class<T> responsePacketType) {
        var uuid = UUID.randomUUID();
        var task = new Task<T>();
        var entry = new QueryEntry<T>(task, responsePacketType);
        queries.put(uuid, entry);
        preparePacket(packet, uuid, TYPE_QUERY).targetAll().build().send();
        return task;
    }

    private ChannelMessage.Builder preparePacket(Packet packet, UUID uuid, byte type) {
        var buf = DataBuf.empty();
        buf.writeByte(type);
        if (type == TYPE_QUERY || type == TYPE_QUERY_RESPONSE) {
            buf.writeUniqueId(uuid);
        }
        PacketSerializer.serialize(packet, buf);
        return prepareMessage(buf);
    }

    private ChannelMessage.Builder prepareMessage(DataBuf buffer) {
        return ChannelMessage.builder().channel(CHANNEL).message(MESSAGE_PACKET).buffer(buffer);
    }

    public <T extends Packet> void registerHandler(Class<T> clazz, PacketHandler<T> handler) {
        handlers.put(clazz, handler);
    }

    public void unregisterHandler(PacketHandler<?> handler) {
        handlers.inverse().remove(handler);
    }

    private record QueryEntry<T>(Task<T> task, Class<T> resultType) {
        void complete(Packet packet) {
            task.complete(resultType.cast(packet));
        }
    }

    public class Listener {

        @EventListener public void handle(ChannelMessageReceiveEvent e) {
            if (!e.channel().equals(CHANNEL)) return;
            if (!e.message().equals(MESSAGE_PACKET)) return;

            DataBuf content = e.content();
            try {
                content.startTransaction();
                byte messageType = content.readByte();

                boolean query = messageType == TYPE_QUERY;
                boolean queryResponse = messageType == TYPE_QUERY_RESPONSE;
                UUID queryId = query || queryResponse ? content.readUniqueId() : null;

                if (queryResponse) {
                    QueryEntry<? extends Packet> entry = queries.getIfPresent(queryId);
                    if (entry == null) return;
                    Packet packet = PacketSerializer.readPacket(content);
                    entry.complete(packet);
                    return;
                }

                Class<? extends Packet> packetClass = PacketSerializer.getClass(content);
                if (handlers.containsKey(packetClass)) {
                    try {
                        Packet received = content.readObject(packetClass);
                        PacketHandler<Packet> handler = (PacketHandler<Packet>) handlers.get(packetClass);
                        Packet response = handler.handle(received);

                        if (query) {
                            DataBuf.Mutable buf = DataBuf.empty();
                            buf.writeByte(TYPE_QUERY_RESPONSE);
                            buf.writeUniqueId(queryId);
                            PacketSerializer.serialize(response, buf);
                            prepareMessage(buf).target(e.sender().toTarget()).build().send();
                        } else if (response != null) {
                            Logger
                                    .getLogger("PacketAPI")
                                    .warning("Gave a response packet to a Packet that isn't a query packet! Handler: " + handler
                                            .getClass()
                                            .getName());
                        }
                    } catch (Throwable ex) {
                        Logger.getLogger("PacketAPI").log(Level.SEVERE, "Packet Handling Failed", ex);
                    }
                }
            } finally {
                if (content.accessible()) content.redoTransaction();
            }
        }
    }
}
