/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit;

import org.bukkit.scheduler.BukkitRunnable;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.pserver.bukkit.command.CommandPServer;
import eu.darkcube.system.pserver.bukkit.command.PServerCommand;
import eu.darkcube.system.pserver.bukkit.packethandler.HandlerAddOwner;
import eu.darkcube.system.pserver.bukkit.packethandler.HandlerAddPServer;
import eu.darkcube.system.pserver.bukkit.packethandler.HandlerDataUpdate;
import eu.darkcube.system.pserver.bukkit.packethandler.HandlerRemoveOwner;
import eu.darkcube.system.pserver.bukkit.packethandler.HandlerRemovePServer;
import eu.darkcube.system.pserver.bukkit.packethandler.HandlerUpdateInfo;
import eu.darkcube.system.pserver.common.ServiceInfoUtil;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PacketManager;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperAddPServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperDataUpdate;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemoveOwner;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperRemovePServer;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUniqueId;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUpdateInfo;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeGetUniqueId;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeSetRunning;

public class PServerWrapper extends Plugin {

	private static PServerWrapper instance;
	private static PServerCommand PServerCommand;
	// private UniqueId id;

	public PServerWrapper() {
		instance = this;
	}

	@Override
	public void onLoad() {

		WrapperUniqueIdProvider.init();
		WrapperServiceInfoUtil.init();
		WrapperPServerProvider.init();

		PacketManager pm = PacketManager.getInstance();
		pm.registerHandler(PacketNodeWrapperAddPServer.class, new HandlerAddPServer());
		pm.registerHandler(PacketNodeWrapperRemovePServer.class, new HandlerRemovePServer());
		pm.registerHandler(PacketNodeWrapperUpdateInfo.class, new HandlerUpdateInfo());
		pm.registerHandler(PacketNodeWrapperAddOwner.class, new HandlerAddOwner());
		pm.registerHandler(PacketNodeWrapperRemoveOwner.class, new HandlerRemoveOwner());
		pm.registerHandler(PacketNodeWrapperDataUpdate.class, new HandlerDataUpdate());

		UniqueId id;
		try {
			id = new PacketWrapperNodeGetUniqueId(Wrapper.getInstance().getServiceId())
					.sendQuery(PacketNodeWrapperUniqueId.class).getUniqueId();
			ServiceInfoSnapshot info = Wrapper.getInstance().getCurrentServiceInfoSnapshot();
			info.setProperty(ServiceInfoUtil.property_uid, id);
			Wrapper.getInstance().publishServiceInfoUpdate(info);
			System.out.println("[PSERVER] LOADING PSERVER...");
			WrapperPServerProvider.getInstance().pserver =
					WrapperPServerProvider.getInstance().getPServer(id);
			System.out.println("[PSERVER] PSERVER ID: " + id);
		} catch (Exception ex) {
			System.out.println("[PSERVER] LOADING PSERVER API...");
		}
	}

	@Override
	public void onEnable() {
		CommandAPI.enable(this, new CommandPServer());
		if (WrapperPServerProvider.getInstance().isPServer()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					System.out.println("[PServer] Set status as RUNNING");
					new PacketWrapperNodeSetRunning(
							WrapperPServerProvider.getInstance().getCurrentPServer().getId())
									.sendAsync();
				}
			}.runTaskAsynchronously(PServerWrapper.getInstance());
		}
	}

	@Override
	public void onDisable() {

	}

	@Override
	public String getCommandPrefix() {
		return "PServer";
	}

	public static void setPServerCommand(PServerCommand pServerCommand) {
		PServerWrapper.PServerCommand = pServerCommand;
	}

	public static PServerCommand getPServerCommand() {
		return PServerCommand;
	}

	public static PServerWrapper getInstance() {
		return instance;
	}
}
