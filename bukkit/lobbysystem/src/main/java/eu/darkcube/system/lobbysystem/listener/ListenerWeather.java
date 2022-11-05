package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ListenerWeather extends BaseListener {

	@EventHandler
	public void handle(WeatherChangeEvent e) {
		if(e.toWeatherState()) {
			e.getWorld().setThundering(false);
			e.getWorld().setStorm(false);
			e.setCancelled(true);
		}
	}
}
