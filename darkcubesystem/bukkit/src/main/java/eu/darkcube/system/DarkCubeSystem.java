/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.arguments.EntityOptions;
import eu.darkcube.system.link.LinkManager;
import eu.darkcube.system.link.cloudnet.CloudNetLink;
import eu.darkcube.system.link.luckperms.LuckPermsLink;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.BukkitUserAPI;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.AsyncExecutor;
import eu.darkcube.system.version.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.Objects;

public final class DarkCubeSystem extends DarkCubePlugin implements Listener {
	private static DarkCubeSystem instance;
	private final LinkManager linkManager = new LinkManager();

	public DarkCubeSystem() {
		super("system");
		DarkCubePlugin.systemPlugin(this);
		instance = this;
	}

	public static DarkCubeSystem getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		VersionSupport.getVersion();
		AsyncExecutor.start();
		EntityOptions.registerOptions();
		PacketAPI.init();
		CommandAPI.init(this);
		linkManager.addLink(LuckPermsLink::new);
		linkManager.addLink(CloudNetLink::new);
	}

	@Override
	public void onDisable() {
		UserAPI.getInstance().loadedUsersForEach(user -> UserAPI.getInstance().unloadUser(user));
		AsyncExecutor.stop();
		AdventureSupport.audienceProvider().close();
		linkManager.unregisterLinks();
	}

	@Override
	public void onEnable() {
		BukkitUserAPI.init();
		Bukkit.getPluginManager().registerEvents(this, this);
		AdventureSupport.audienceProvider(); // Initializes adventure
		linkManager.enableLinks();
	}

	@EventHandler
	private void handle(PlayerKickEvent event) {
		if (Objects.equals(event.getReason(), "disconnect.spam")) {
			event.setCancelled(true);
		}
	}
}
