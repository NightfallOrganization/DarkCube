/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby;

import eu.darkcube.minigame.woolbattle.listener.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ListenerWeatherChange extends Listener<WeatherChangeEvent> {

    @Override
    @EventHandler
    public void handle(WeatherChangeEvent e) {
        if (e.toWeatherState()) e.setCancelled(true);
    }
}
