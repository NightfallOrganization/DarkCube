/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.node.userapi;

import java.util.UUID;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.modules.bridge.player.CloudOfflinePlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.darkcube.system.module.userapi.handler.HandlerPlayerLogin;
import eu.darkcube.system.impl.common.userapi.CommonPersistentDataStorage;
import eu.darkcube.system.userapi.packets.PacketNWUpdateName;
import eu.darkcube.system.userapi.packets.PacketWNPlayerLogin;
import eu.darkcube.system.impl.common.userapi.CommonUser;
import eu.darkcube.system.impl.common.userapi.CommonUserAPI;
import eu.darkcube.system.impl.common.userapi.CommonUserData;
import eu.darkcube.system.impl.common.userapi.UserLocalPacketHandlers;
import eu.darkcube.system.impl.common.userapi.UserLocalPersistentDataStorage;

@Singleton public class NodeUserAPI extends CommonUserAPI {
    private final PlayerManager playerManager;
    private final Database database;
    private final UserLocalPacketHandlers packetHandlers = new UserLocalPacketHandlers(this);
    private final NodeDataSaver dataSaver;

    @Inject public NodeUserAPI(DatabaseProvider databaseProvider, ServiceRegistry serviceRegistry) {
        this.playerManager = serviceRegistry.firstProvider(PlayerManager.class);
        this.database = databaseProvider.database("userapi_users");
        this.dataSaver = new NodeDataSaver(this.database);
        this.packetHandlers.handlerGroup().registerHandler(PacketWNPlayerLogin.class, new HandlerPlayerLogin(this));
    }

    public void updateName(UUID uniqueId, String playerName) {
        var user = user(uniqueId);
        user.userData().name(playerName);
        ((UserLocalPersistentDataStorage) user.persistentData()).name(playerName);
        new PacketNWUpdateName(uniqueId, playerName).sendAsync();
    }

    @Override protected CommonUser loadUser(UUID uniqueId) {
        var data = this.database.get(uniqueId.toString());
        if (data == null) data = Document.emptyDocument();
        String name = data.getString("name");
        if (name != null && uniqueId.toString().startsWith(name)) name = null;
        if (name == null) {
            var offlinePlayer = this.playerManager.offlinePlayer(uniqueId);
            if (offlinePlayer != null) name = offlinePlayer.name();
        }
        if (name == null) {
            name = uniqueId.toString().substring(0, 16);
        }
        CommonPersistentDataStorage persistentData = new UserLocalPersistentDataStorage(uniqueId, name, data.readDocument("persistentData"));
        persistentData.addUpdateNotifier(this.dataSaver.saveNotifier());
        var userData = new CommonUserData(uniqueId, name, persistentData);
        return new NodeUser(userData);
    }

    public void exit() {
        this.dataSaver.exit();
    }

    public void init() {
        this.dataSaver.init();
        this.packetHandlers.registerHandlers();
    }
}
