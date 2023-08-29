/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.packetapi.HandlerGroup;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.packets.PacketWNQueryUser;
import eu.darkcube.system.userapi.packets.PacketWNUserPersistentDataLoad;
import eu.darkcube.system.userapi.packets.PacketWNUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketWNUserPersistentDataSet;

public class UserLocalPacketHandlers {
    private final HandlerGroup handlerGroup = new HandlerGroup();
    private final CommonUserAPI api;

    public UserLocalPacketHandlers(CommonUserAPI api) {
        this.api = api;
        this.handlerGroup.registerHandler(PacketWNQueryUser.class, packet -> {
            CommonUser user = api.userCache.get(packet.uniqueId());
            return new PacketWNQueryUser.Result(user.name(), user.persistentData().storeToJsonDocument());
        });
        this.handlerGroup.registerHandler(PacketWNUserPersistentDataSet.class, packet -> {
            CommonUser user = api.userCache.get(packet.uniqueId());
            user.userData().persistentData().merge(packet.data());
            return null;
        });
        this.handlerGroup.registerHandler(PacketWNUserPersistentDataRemove.class, packet -> {
            CommonUser user = api.userCache.get(packet.uniqueId());
            user.userData().persistentData().remove(packet.key());
            return null;
        });
        this.handlerGroup.registerHandler(PacketWNUserPersistentDataLoad.class, packet -> {
            CommonUser user = api.userCache.get(packet.uniqueId());
            user.persistentData().loadFromJsonDocument(packet.data());
            return null;
        });
    }

    public void registerHandlers() {
        PacketAPI.getInstance().registerGroup(handlerGroup);
    }

    public void unregisterHandlers() {
        PacketAPI.getInstance().unregisterGroup(handlerGroup);
    }
}
