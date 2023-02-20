/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import com.google.common.reflect.TypeToken;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.pserver.cloudnet.command.CommandPServers;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.cloudnet.packethandler.*;
import eu.darkcube.system.pserver.cloudnet.packethandler.storage.*;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packets.wn.*;
import eu.darkcube.system.pserver.common.packets.wn.storage.*;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PServerModule extends DriverModule {

	public static final String PLUGIN_NAME = new File(
			PServerModule.class.getProtectionDomain().getCodeSource().getLocation()
					.getPath()).getName();
	private static PServerModule instance;
	public Listener listener;

	public String sqlDatabase;

	private List<String> deploymentExclusions;

	public PServerModule() {
		PServerModule.instance = this;
	}

	public static String getSelf() {
		return PServerModule.getCloudNet().getCurrentNetworkClusterNodeInfoSnapshot().getNode()
				.getUniqueId();
	}

	public static CloudNet getCloudNet() {
		return CloudNet.getInstance();
	}

	public static PServerModule getInstance() {
		return PServerModule.instance;
	}

	public static Collection<UniqueId> getUsedPServerIDs() {
		return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getUsedPServerIDs();
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.LOADED)
	public void loadConfig() {
		this.sqlDatabase = this.getConfig().getString("database", "h2");
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
	public void load() {
		this.getLogger().info("Enabling module PServer");
		NodeServiceInfoUtil.init();
		NodePServerProvider.init();
		NodeUniqueIdProvider.init();
		DatabaseProvider.register("pserver", new PServerDatabase());

		CloudNet.getInstance().getCommandMap().registerCommand(new CommandPServers());

		PacketAPI pm = PacketAPI.getInstance();
		pm.registerHandler(PacketType.class, new HandlerType());
		pm.registerHandler(PacketTaskName.class, new HandlerTaskName());
		pm.registerHandler(PacketStop.class, new HandlerStop());
		pm.registerHandler(PacketState.class, new HandlerState());
		pm.registerHandler(PacketStartedAt.class, new HandlerStartedAt());
		pm.registerHandler(PacketStart.class, new HandlerStart());
		pm.registerHandler(PacketServerName.class, new HandlerServerName());
		pm.registerHandler(PacketRemoveOwner.class, new HandlerRemoveOwner());
		pm.registerHandler(PacketPServersByOwner.class, new HandlerPServersByOwner());
		pm.registerHandler(PacketPServers.class, new HandlerPServers());
		pm.registerHandler(PacketOwners.class, new HandlerOwners());
		pm.registerHandler(PacketOntime.class, new HandlerOntime());
		pm.registerHandler(PacketOnlinePlayers.class, new HandlerOnlinePlayers());
		pm.registerHandler(PacketExists.class, new HandlerExists());
		pm.registerHandler(PacketCreateSnapshot.class, new HandlerCreateSnapshot());
		pm.registerHandler(PacketCreate.class, new HandlerCreate());
		pm.registerHandler(PacketConnectPlayer.class, new HandlerConnectPlayer());
		pm.registerHandler(PacketAddOwner.class, new HandlerAddOwner());
		pm.registerHandler(PacketAccessLevelSet.class, new HandlerAccessLevelSet());
		pm.registerHandler(PacketAccessLevel.class, new HandlerAccessLevel());

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

		this.getLogger().info("PServer initializing!");

		this.deploymentExclusions =
				this.getConfig().get("deploymentExclusions", new TypeToken<List<String>>() {

					private static final long serialVersionUID = 1L;

				}.getType(), Collections.singletonList("paper.jar"));
		this.saveConfig();
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
	public void start() {
		PServerModule.getCloudNet().getEventManager()
				.registerListener((this.listener = new Listener()));
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STOPPED)
	public void stop() {

	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.UNLOADED)
	public void unload() {

	}

	public void addDeploymentExclusion(String exclusion) {
		this.deploymentExclusions.add(exclusion);
		this.getConfig().append("deploymentExclusions", this.deploymentExclusions);
		this.saveConfig();
	}

	public void removeDeploymentExclusion(String exclusion) {
		this.deploymentExclusions.remove(exclusion);
		this.getConfig().append("deploymentExclusions", this.deploymentExclusions);
		this.saveConfig();
	}

	public List<String> getDeploymentExclusions() {
		return Collections.unmodifiableList(this.deploymentExclusions);
	}

}
