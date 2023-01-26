package eu.darkcube.system.miners.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ListenerChatMessage implements Listener {

	@EventHandler
	public void onChatMessage(AsyncPlayerChatEvent e) {
		String message = e.getPlayer().getCustomName() + ChatColor.RESET + ": " + e.getMessage();
		Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
		e.setCancelled(true);
	}

}