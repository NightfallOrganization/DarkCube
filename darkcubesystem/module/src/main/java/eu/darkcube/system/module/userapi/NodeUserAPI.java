/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.userapi;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.modules.bridge.player.CloudOfflinePlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.darkcube.system.userapi.*;

import java.util.UUID;

@Singleton public class NodeUserAPI extends CommonUserAPI {
    private final PlayerManager playerManager;
    private final Database database;
    private final UserLocalPacketHandlers packetHandlers = new UserLocalPacketHandlers(this);
    private final NodeDataSaver dataSaver;

    @Inject public NodeUserAPI(DatabaseProvider databaseProvider, ServiceRegistry serviceRegistry) {
        this.playerManager = serviceRegistry.firstProvider(PlayerManager.class);
        this.database = databaseProvider.database("userapi_users");
        this.dataSaver = new NodeDataSaver(this.database);
    }

    @Override protected CommonUser loadUser(UUID uniqueId) {
        Document data = this.database.get(uniqueId.toString());
        if (data == null) data = Document.emptyDocument();
        String name;
        CloudOfflinePlayer offlinePlayer = this.playerManager.offlinePlayer(uniqueId);
        if (offlinePlayer != null) name = offlinePlayer.name();
        else if (data.contains("name")) name = data.getString("name");
        else name = uniqueId.toString().substring(0, 16);
        CommonPersistentDataStorage persistentData = new UserLocalPersistentDataStorage(uniqueId, name, data.readDocument("persistentData"));
        persistentData.addUpdateNotifier(this.dataSaver.saveNotifier());
        CommonUserData userData = new CommonUserData(uniqueId, name, persistentData);
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
