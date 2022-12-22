/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.arguments.EntityOptions;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.util.AsyncExecutor;
import eu.darkcube.system.version.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public final class DarkCubeSystem extends DarkCubePlugin implements Listener {
	private static DarkCubeSystem instance;

	public DarkCubeSystem() {
		instance = this;
	}

	@Override
	public void onLoad() {
		VersionSupport.init();
		AsyncExecutor.start();
		EntityOptions.registerOptions();
		PacketAPI.init();
		CommandAPI.init(this);
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		AsyncExecutor.stop();
	}

	@EventHandler
	public void handle(PlayerKickEvent event) {
		if (event.getReason() == "disconnect.spam") {
			event.setCancelled(true);
		}
	}

	public static DarkCubeSystem getInstance() {
		return instance;
	}

}
