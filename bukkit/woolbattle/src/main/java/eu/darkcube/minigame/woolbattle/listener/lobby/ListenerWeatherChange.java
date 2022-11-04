package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerWeatherChange extends Listener<WeatherChangeEvent> {

	@Override
	@EventHandler
	public void handle(WeatherChangeEvent e) {
		if (e.toWeatherState())
			e.setCancelled(true);
	}
}