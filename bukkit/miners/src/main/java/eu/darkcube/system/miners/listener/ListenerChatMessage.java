package eu.darkcube.system.miners.listener;

import eu.darkcube.system.miners.Miners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ListenerChatMessage implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        String message = e.getPlayer().getCustomName() + ChatColor.RESET + ": " + e.getMessage();

        if (ListenerSpectators.isSpectatorPlayer(e.getPlayer()) && Miners.getGamephase() < 3)
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (ListenerSpectators.isSpectatorPlayer(p))
                    p.sendMessage(message);
            });
        else
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));

    }

}
