/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ListenerWeather extends BaseListener {

	@EventHandler
	public void handle(WeatherChangeEvent e) {
		if (e.toWeatherState()) {
			e.setCancelled(true);
			e.getWorld().setWeatherDuration(Integer.MAX_VALUE);
		}
	}

	@EventHandler
	public void handle(BlockFormEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(ThunderChangeEvent e) {
		if (e.toThunderState()) {
			e.setCancelled(true);
		}
	}
}
