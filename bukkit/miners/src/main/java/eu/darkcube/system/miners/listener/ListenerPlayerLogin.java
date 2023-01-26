package eu.darkcube.system.miners.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import eu.darkcube.system.miners.Miners;

public class ListenerPlayerLogin implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (Miners.getGamephase() != 0)
            return;
        if (Bukkit.getOnlinePlayers().size() >= Miners.getMinersConfig().MAX_PLAYERS)
            e.disallow(Result.KICK_FULL, "§cDieser Server ist voll!");
    }

}