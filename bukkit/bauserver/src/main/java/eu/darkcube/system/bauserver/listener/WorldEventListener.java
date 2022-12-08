/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import eu.darkcube.system.bauserver.Main;

public class WorldEventListener implements Listener {

	@EventHandler
	public void handle(WorldLoadEvent event) {
		Main.getInstance().setupWorld(event.getWorld());
	}

	@EventHandler
	public void handle(WeatherChangeEvent event) {
		if (event.getWorld().hasMetadata("mayrain")) {
			return;
		}
		if (event.toWeatherState()) {
			event.setCancelled(true);
			event.getWorld().setWeatherDuration(Integer.MAX_VALUE);
		}
	}

	@EventHandler
	public void handle(BlockPhysicsEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void handle(LeavesDecayEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockFadeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockSpreadEvent event) {
		event.setCancelled(true);
	}

}
