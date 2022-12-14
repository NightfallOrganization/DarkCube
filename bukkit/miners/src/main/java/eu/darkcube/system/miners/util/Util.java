package eu.darkcube.system.miners.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class Util {

	public static void sendActionbar(String message, Player... players) {
		if (message == null)
			return;
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + message + "\"}"), (byte) 2);
		for (Player p : players) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void sendActionbarToAll(String message, Player... exclude) {
		if (message == null)
			return;
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + message + "\"}"), (byte) 2);
		List<Player> excludeList = Arrays.asList(exclude);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!excludeList.contains(p))
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static String secondsToTime(int seconds, boolean showZeroHours, boolean showZeroMins, String color1,
			String color2) {
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
