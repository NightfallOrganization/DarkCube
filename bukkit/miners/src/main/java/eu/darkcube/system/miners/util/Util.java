package eu.darkcube.system.miners.util;

import eu.darkcube.system.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Util {

	public static void sendActionbar(String message, Player... players) {
		if (message == null)
			return;
		ChatUtils.ChatEntry.build(new ChatUtils.ChatEntry.Builder().text(message).build())
				.send(players);
	}

	public static void sendActionbarToAll(String message, Player... exclude) {
		if (message == null)
			return;
		List<Player> excludes = Arrays.asList(exclude);
		sendActionbar(message, Bukkit.getOnlinePlayers().stream().filter(p -> !excludes.contains(p))
				.toArray(i -> new Player[i]));
	}

	public static String secondsToTime(int seconds, boolean showZeroHours, boolean showZeroMins,
			String color1, String color2) {
		if (color1 == null || color2 == null)
			return "Color null";
		if (seconds < 0)
			return "seconds<0";
		int minutes = 0;
		int hours = 0;
		StringBuilder returnString = new StringBuilder(color1);
		if (seconds >= 60) { // only calc mins if secs >60
			minutes = (seconds - (seconds % 60)) / 60;
			seconds = seconds % 60;
		}
		if (minutes >= 60 || showZeroHours) {
			hours = (minutes - (minutes % 60)) / 60;
			minutes = minutes % 60;
			if (hours < 10) {
				if (hours != 0 || showZeroHours) {
					returnString.append(0).append(hours);
				}
			} else {
				returnString.append(hours);
			}
			returnString.append(color2).append(":").append(color1);
		}
		if (minutes < 10) {
			if (minutes != 0 || showZeroMins || showZeroHours || hours != 0) {
				returnString.append(0).append(minutes).append(color2).append(":").append(color1);
			}
		} else {
			returnString.append(minutes).append(color2).append(":").append(color1);
		}
		if (seconds < 10) {
			returnString.append(0).append(seconds);
		} else {
			returnString.append(seconds);
		}
		return returnString.toString();
	}

	public static String secondsToTime(int seconds, boolean showZeroHours, boolean showZeroMins) {
		return secondsToTime(seconds, showZeroHours, showZeroMins, "", "");
	}

}
