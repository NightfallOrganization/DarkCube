/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.common.userapi;

import java.util.UUID;
import java.util.function.BiConsumer;

import eu.darkcube.system.packetapi.HandlerGroup;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.packets.PacketNWUpdateName;
import eu.darkcube.system.userapi.packets.PacketNWUserPersistentDataMerge;
import eu.darkcube.system.userapi.packets.PacketNWUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketNWUserPersistentDataUpdate;

public class CommonRemoteUserPacketHandler {

    private final HandlerGroup handlerGroup = new HandlerGroup();
    private final CommonUserAPI api;

    public CommonRemoteUserPacketHandler(CommonUserAPI api) {
        this.api = api;
        this.handlerGroup.registerHandler(PacketNWUserPersistentDataRemove.class, packet -> {
            ifPresent(packet.uniqueId(), packet, (storage, data) -> storage.remove(data.key()));
            return null;
        });
        this.handlerGroup.registerHandler(PacketNWUserPersistentDataUpdate.class, packet -> {
            ifPresent(packet.uniqueId(), packet, (storage, data) -> storage.update(data.data()));
            return null;
        });
        this.handlerGroup.registerHandler(PacketNWUserPersistentDataMerge.class, packet -> {
            ifPresent(packet.uniqueId(), packet, (storage, data) -> storage.merge(packet.data()));
            return null;
        });
        this.handlerGroup.registerHandler(PacketNWUpdateName.class, packet -> {
            var user = api.userCache.getIfPresent(packet.uniqueId());
            if (user != null) {
                user.userData().name(packet.playerName());
            }
            return null;
        });
    }

    private <T> void ifPresent(UUID uniqueId, T packet, BiConsumer<CommonPersistentDataStorage, T> consumer) {
        var user = api.userCache.getIfPresent(uniqueId);
        if (user == null) return;
        consumer.accept(user.userData().persistentData(), packet);
    }

    public void registerHandlers() {
        PacketAPI.instance().registerGroup(handlerGroup);
    }

    public void unregisterHandlers() {
        PacketAPI.instance().unregisterGroup(handlerGroup);
    }
}
