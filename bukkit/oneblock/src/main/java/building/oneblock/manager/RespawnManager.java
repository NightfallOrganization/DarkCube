package building.oneblock.manager;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import static building.oneblock.manager.WorldManager.SPAWN;

public class RespawnManager implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase(SPAWN.getName())) {
            Location respawnLocation = new Location(event.getPlayer().getWorld(), 0, 100, 0);
            event.setRespawnLocation(respawnLocation);
        }
    }
}
