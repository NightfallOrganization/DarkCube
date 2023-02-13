/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.link.cloudnet;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.dytanic.cloudnet.wrapper.event.service.ServiceInfoSnapshotConfigureEvent;
import eu.darkcube.system.DarkCubeBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener implements org.bukkit.event.Listener {

	@EventListener
	public void handle(ServiceInfoSnapshotConfigureEvent event) {
		JsonDocument properties = event.getServiceInfoSnapshot().getProperties();
		properties.append("gameState", DarkCubeBukkit.gameState().toString())
				.append("playingPlayers", DarkCubeBukkit.playingPlayers().get())
				.append("spectatingPlayers", DarkCubeBukkit.spectatingPlayers().get())
				.append("maxPlayingPlayers", DarkCubeBukkit.maxPlayingPlayers().get())
				.append("displayName", DarkCubeBukkit.displayName());
	}

	@EventHandler
	public void handle(PlayerJoinEvent event) {
		if (DarkCubeBukkit.autoConfigure()) {
			DarkCubeBukkit.playingPlayers().incrementAndGet();
			Wrapper.getInstance().publishServiceInfoUpdate();
		}
	}

	@EventHandler
	public void handle(PlayerQuitEvent event) {
		if (DarkCubeBukkit.autoConfigure()) {
			DarkCubeBukkit.playingPlayers().decrementAndGet();
			Wrapper.getInstance().publishServiceInfoUpdate();
		}
	}

}
