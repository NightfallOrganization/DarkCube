/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.reflect.TypeToken;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import eu.darkcube.system.pserver.cloudnet.command.CommandPServers;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerAddOwner;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerClearOwners;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerConnectPlayer;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerCreatePServer;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerDelete;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerGetData;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerGetOwners;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerGetPServer;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerGetPServersOfPlayer;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerGetUniqueId;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerNewName;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerNewUniqueId;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerRemove;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerRemoveOwner;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerRetrievePServers;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerSetRunning;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerStart;
import eu.darkcube.system.pserver.cloudnet.packethandler.HandlerStop;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PacketManager;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeAddOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeClearOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeConnectPlayer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeCreatePServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeDelete;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetData;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetOwners;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetPServersOfPlayer;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetUniqueId;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeNewName;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeNewUniqueId;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRemove;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRemoveOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeRetrievePServers;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeSetRunning;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeStart;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeStop;

public class PServerModule extends DriverModule {

	private static PServerModule instance;

	public static final String PLUGIN_NAME = new File(
			PServerModule.class.getProtectionDomain().getCodeSource().getLocation().getPath())
					.getName();

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

		PacketManager pm = PacketManager.getInstance();
		pm.registerHandler(PacketWrapperNodeAddOwner.class, new HandlerAddOwner());
		pm.registerHandler(PacketWrapperNodeClearOwners.class, new HandlerClearOwners());
		pm.registerHandler(PacketWrapperNodeConnectPlayer.class, new HandlerConnectPlayer());
		pm.registerHandler(PacketWrapperNodeCreatePServer.class, new HandlerCreatePServer());
		pm.registerHandler(PacketWrapperNodeDelete.class, new HandlerDelete());
		pm.registerHandler(PacketWrapperNodeGetOwners.class, new HandlerGetOwners());
		pm.registerHandler(PacketWrapperNodeGetPServer.class, new HandlerGetPServer());
		pm.registerHandler(PacketWrapperNodeGetPServersOfPlayer.class,
				new HandlerGetPServersOfPlayer());
		pm.registerHandler(PacketWrapperNodeGetUniqueId.class, new HandlerGetUniqueId());
		pm.registerHandler(PacketWrapperNodeNewName.class, new HandlerNewName());
		pm.registerHandler(PacketWrapperNodeNewUniqueId.class, new HandlerNewUniqueId());
		pm.registerHandler(PacketWrapperNodeRemove.class, new HandlerRemove());
		pm.registerHandler(PacketWrapperNodeRemoveOwner.class, new HandlerRemoveOwner());
		pm.registerHandler(PacketWrapperNodeRetrievePServers.class, new HandlerRetrievePServers());
		pm.registerHandler(PacketWrapperNodeSetRunning.class, new HandlerSetRunning());
		pm.registerHandler(PacketWrapperNodeStart.class, new HandlerStart());
		pm.registerHandler(PacketWrapperNodeStop.class, new HandlerStop());
		pm.registerHandler(PacketWrapperNodeGetData.class, new HandlerGetData());

		this.getLogger().info("PServer initializing!");
		AsyncExecutor.start();

		this.deploymentExclusions =
				this.getConfig().get("deploymentExclusions", new TypeToken<List<String>>() {

					private static final long serialVersionUID = 1L;

				}.getType(), Arrays.asList("paper.jar"));
		this.saveConfig();
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
	public void start() {
		PServerModule.getCloudNet().getEventManager()
				.registerListener((this.listener = new Listener()));
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STOPPED)
	public void stop() {
		AsyncExecutor.shutdown();
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

	public static final CloudNet getCloudNet() {
		return CloudNet.getInstance();
	}

	public static PServerModule getInstance() {
		return PServerModule.instance;
	}

	public static Collection<UniqueId> getCurrentPServerIDs() {
		return PServerProvider.getInstance().getPServers().stream().map(PServer::getId)
				.collect(Collectors.toList());
	}

	public static Collection<UniqueId> getUsedPServerIDs() {
		return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getUsedPServerIDs();
	}

}
