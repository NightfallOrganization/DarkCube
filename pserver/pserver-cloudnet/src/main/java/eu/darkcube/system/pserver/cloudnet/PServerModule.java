/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.node.command.CommandProvider;
import eu.darkcube.system.cloudnet.packetapi.HandlerGroup;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.pserver.cloudnet.command.CommandPServers;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerAccessLevel;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerAccessLevelSet;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerAddOwner;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerConnectPlayer;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerCreate;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerCreateSnapshot;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerExists;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerGetUniqueId;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerOnlinePlayers;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerOnlinePlayersSet;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerOntime;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerOwners;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerPServers;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerPServersByOwner;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerRegisteredPServers;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerRemoveOwner;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerServerName;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerSetRunning;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerStart;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerStartedAt;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerState;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerStop;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerTaskName;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerType;
import eu.darkcube.system.pserver.common.packets.wn.PacketAccessLevel;
import eu.darkcube.system.pserver.common.packets.wn.PacketAccessLevelSet;
import eu.darkcube.system.pserver.common.packets.wn.PacketAddOwner;
import eu.darkcube.system.pserver.common.packets.wn.PacketConnectPlayer;
import eu.darkcube.system.pserver.common.packets.wn.PacketCreate;
import eu.darkcube.system.pserver.common.packets.wn.PacketCreateSnapshot;
import eu.darkcube.system.pserver.common.packets.wn.PacketExists;
import eu.darkcube.system.pserver.common.packets.wn.PacketGetUniqueId;
import eu.darkcube.system.pserver.common.packets.wn.PacketOnlinePlayers;
import eu.darkcube.system.pserver.common.packets.wn.PacketOnlinePlayersSet;
import eu.darkcube.system.pserver.common.packets.wn.PacketOntime;
import eu.darkcube.system.pserver.common.packets.wn.PacketOwners;
import eu.darkcube.system.pserver.common.packets.wn.PacketPServers;
import eu.darkcube.system.pserver.common.packets.wn.PacketPServersByOwner;
import eu.darkcube.system.pserver.common.packets.wn.PacketRegisteredPServers;
import eu.darkcube.system.pserver.common.packets.wn.PacketRemoveOwner;
import eu.darkcube.system.pserver.common.packets.wn.PacketServerName;
import eu.darkcube.system.pserver.common.packets.wn.PacketSetRunning;
import eu.darkcube.system.pserver.common.packets.wn.PacketStart;
import eu.darkcube.system.pserver.common.packets.wn.PacketStartedAt;
import eu.darkcube.system.pserver.common.packets.wn.PacketState;
import eu.darkcube.system.pserver.common.packets.wn.PacketStop;
import eu.darkcube.system.pserver.common.packets.wn.PacketTaskName;
import eu.darkcube.system.pserver.common.packets.wn.PacketType;

public class PServerModule extends DriverModule {

    public static final String PLUGIN_NAME = new File(PServerModule.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    private static PServerModule instance;
    public Listener listener;
    public String sqlDatabase;
    private Logger logger = Logger.getLogger("PServer");
    private PServerConfiguration configuration;
    private List<Pattern> compiledDeploymentExclusions = null;

    public PServerModule() {
        PServerModule.instance = this;
    }

    public static PServerModule getInstance() {
        return PServerModule.instance;
    }

    @ModuleTask(order = Byte.MAX_VALUE, lifecycle = ModuleLifeCycle.LOADED)
    public void loadConfig() {
        configuration = this.readConfig(PServerConfiguration.class, () -> new PServerConfiguration("h2", defaultExclusions()), DocumentFactory.json());
        this.sqlDatabase = configuration.database();
    }

    private Set<String> defaultExclusions() {
        try {
            var in = getClass().getClassLoader().getResourceAsStream("default_deployment_exclusions.txt");
            if (in == null) return Collections.emptySet();
            var data = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            var lines = data.split("\\n");
            in.close();
            return Set.of(lines);
        } catch (Throwable t) {
            t.printStackTrace();
            return Collections.emptySet();
        }
    }

    @ModuleTask(order = Byte.MAX_VALUE, lifecycle = ModuleLifeCycle.STARTED)
    public void load(EventManager eventManager, CommandProvider commandProvider) {
        //        ClassLoaderFixRelocation.load(logger, eventManager, commandProvider);
        logger.info("Enabling module PServer");
        NodeServiceInfoUtil.init();
        NodePServerProvider.init();
        NodeUniqueIdProvider.init();
        DatabaseProvider.register("pserver", new PServerDatabase());

        var handlerGroup = new HandlerGroup();
        handlerGroup.registerHandler(PacketType.class, new HandlerType());
        handlerGroup.registerHandler(PacketTaskName.class, new HandlerTaskName());
        handlerGroup.registerHandler(PacketStop.class, new HandlerStop());
        handlerGroup.registerHandler(PacketState.class, new HandlerState());
        handlerGroup.registerHandler(PacketStartedAt.class, new HandlerStartedAt());
        handlerGroup.registerHandler(PacketStart.class, new HandlerStart());
        handlerGroup.registerHandler(PacketSetRunning.class, new HandlerSetRunning());
        handlerGroup.registerHandler(PacketServerName.class, new HandlerServerName());
        handlerGroup.registerHandler(PacketRemoveOwner.class, new HandlerRemoveOwner());
        handlerGroup.registerHandler(PacketPServersByOwner.class, new HandlerPServersByOwner());
        handlerGroup.registerHandler(PacketPServers.class, new HandlerPServers());
        handlerGroup.registerHandler(PacketOwners.class, new HandlerOwners());
        handlerGroup.registerHandler(PacketOntime.class, new HandlerOntime());
        handlerGroup.registerHandler(PacketOnlinePlayersSet.class, new HandlerOnlinePlayersSet());
        handlerGroup.registerHandler(PacketOnlinePlayers.class, new HandlerOnlinePlayers());
        handlerGroup.registerHandler(PacketGetUniqueId.class, new HandlerGetUniqueId());
        handlerGroup.registerHandler(PacketExists.class, new HandlerExists());
        handlerGroup.registerHandler(PacketCreateSnapshot.class, new HandlerCreateSnapshot());
        handlerGroup.registerHandler(PacketCreate.class, new HandlerCreate());
        handlerGroup.registerHandler(PacketConnectPlayer.class, new HandlerConnectPlayer());
        handlerGroup.registerHandler(PacketAddOwner.class, new HandlerAddOwner());
        handlerGroup.registerHandler(PacketAccessLevelSet.class, new HandlerAccessLevelSet());
        handlerGroup.registerHandler(PacketAccessLevel.class, new HandlerAccessLevel());

        handlerGroup.registerHandler(PacketRegisteredPServers.class, new HandlerRegisteredPServers());

        PacketAPI.instance().registerGroup(handlerGroup);

        eventManager.registerListener((PServerModule.getInstance().listener = new Listener()));
        commandProvider.register(CommandPServers.class);
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void stop() {
        NodePServerProvider.instance().cleanup();
    }

    private void invalidateCompiledDeploymentExclusions() {
        compiledDeploymentExclusions = null;
    }

    public List<Pattern> compiledDeploymentExclusions() {
        if (compiledDeploymentExclusions == null) {
            compiledDeploymentExclusions = configuration.deploymentExclusions().stream().map(Pattern::compile).collect(Collectors.toList());
        }
        return compiledDeploymentExclusions;
    }

    public boolean addDeploymentExclusion(String exclusion) {
        var changed = configuration.deploymentExclusions().add(exclusion);
        if (changed) {
            saveConfig();
            invalidateCompiledDeploymentExclusions();
        }
        return changed;
    }

    public boolean removeDeploymentExclusion(String exclusion) {
        var changed = configuration.deploymentExclusions().remove(exclusion);
        if (changed) {
            saveConfig();
            invalidateCompiledDeploymentExclusions();
        }
        return changed;
    }

    private void saveConfig() {
        writeConfig(Document.newJsonDocument().appendTree(configuration));
    }
}
