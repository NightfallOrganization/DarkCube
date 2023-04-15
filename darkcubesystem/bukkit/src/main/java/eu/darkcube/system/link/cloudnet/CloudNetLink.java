/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.link.cloudnet;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.system.DarkCubeBukkit;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.link.Link;
import eu.darkcube.system.util.GameState;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.logging.Logger;

public class CloudNetLink extends Link {
	private final Logger logger = Logger.getLogger("CloudNetLink");
	private Listener listener;

	public CloudNetLink() throws Throwable {
	}

	@Override
	protected void link() {
		CloudNetDriver.getInstance().getEventManager().registerListener(listener = new Listener());
		CloudNetDriver.getInstance().getTaskExecutor()
				.execute(Wrapper.getInstance()::publishServiceInfoUpdate);

	}

	@Override
	protected void onEnable() {
		Bukkit.getPluginManager().registerEvents(listener, DarkCubeSystem.getInstance());
		Bukkit.getScheduler().runTask(DarkCubeSystem.getInstance(), () -> {
			if (DarkCubeBukkit.autoConfigure()) {
				DarkCubeBukkit.gameState(GameState.INGAME);
				Wrapper.getInstance().publishServiceInfoUpdate();
			}
		});
	}

	@Override
	protected void unlink() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(listener);
		HandlerList.unregisterAll(listener);
		listener = null;
	}
}
