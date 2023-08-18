/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.node.command.CommandProvider;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.pserver.cloudnet.command.CommandPServers;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.cloudnet.packethandler.*;
import eu.darkcube.system.pserver.cloudnet.packethandler.storage.*;
import eu.darkcube.system.pserver.common.packets.wn.*;
import eu.darkcube.system.pserver.common.packets.wn.storage.*;

import java.util.logging.Logger;

public class ClassLoaderFixRelocation {

    public static void load(PServerModule module) {
        Logger.getLogger("PServer").info("Enabling module PServer");
        NodeServiceInfoUtil.init();
        NodePServerProvider.init();
        NodeUniqueIdProvider.init();
        DatabaseProvider.register("pserver", new PServerDatabase());

        // TODO
//        CloudNet.getInstance().getCommandMap().registerCommand(new CommandPServers());

        PacketAPI pm = PacketAPI.getInstance();
        pm.registerHandler(PacketType.class, new HandlerType());
        pm.registerHandler(PacketTaskName.class, new HandlerTaskName());
        pm.registerHandler(PacketStop.class, new HandlerStop());
        pm.registerHandler(PacketState.class, new HandlerState());
        pm.registerHandler(PacketStartedAt.class, new HandlerStartedAt());
        pm.registerHandler(PacketStart.class, new HandlerStart());
        pm.registerHandler(PacketSetRunning.class, new HandlerSetRunning());
        pm.registerHandler(PacketServerName.class, new HandlerServerName());
        pm.registerHandler(PacketRemoveOwner.class, new HandlerRemoveOwner());
        pm.registerHandler(PacketPServersByOwner.class, new HandlerPServersByOwner());
        pm.registerHandler(PacketPServers.class, new HandlerPServers());
        pm.registerHandler(PacketOwners.class, new HandlerOwners());
        pm.registerHandler(PacketOntime.class, new HandlerOntime());
        pm.registerHandler(PacketOnlinePlayersSet.class, new HandlerOnlinePlayersSet());
        pm.registerHandler(PacketOnlinePlayers.class, new HandlerOnlinePlayers());
        pm.registerHandler(PacketGetUniqueId.class, new HandlerGetUniqueId());
        pm.registerHandler(PacketExists.class, new HandlerExists());
        pm.registerHandler(PacketCreateSnapshot.class, new HandlerCreateSnapshot());
        pm.registerHandler(PacketCreate.class, new HandlerCreate());
        pm.registerHandler(PacketConnectPlayer.class, new HandlerConnectPlayer());
        pm.registerHandler(PacketAddOwner.class, new HandlerAddOwner());
        pm.registerHandler(PacketAccessLevelSet.class, new HandlerAccessLevelSet());
        pm.registerHandler(PacketAccessLevel.class, new HandlerAccessLevel());

        pm.registerHandler(PacketClearCache.class, new HandlerClearCache());
        pm.registerHandler(PacketClear.class, new HandlerClear());
        pm.registerHandler(PacketGet.class, new HandlerGet());
        pm.registerHandler(PacketGetDef.class, new HandlerGetDef());
        pm.registerHandler(PacketHas.class, new HandlerHas());
        pm.registerHandler(PacketKeys.class, new HandlerKeys());
        pm.registerHandler(PacketLoadFromDocument.class, new HandlerLoadFromDocument());
        pm.registerHandler(PacketRemove.class, new HandlerRemove());
        pm.registerHandler(PacketSet.class, new HandlerSet());
        pm.registerHandler(PacketSetIfNotPresent.class, new HandlerSetIfNotPresent());
        pm.registerHandler(PacketStoreToDocument.class, new HandlerStoreToDocument());

        Logger.getLogger("PServer").info("PServer initializing!");

        InjectionLayer.boot().instance(EventManager.class).registerListener((PServerModule.getInstance().listener = new Listener()));
        InjectionLayer.boot().instance(CommandProvider.class).register(CommandPServers.class);
    }

}
