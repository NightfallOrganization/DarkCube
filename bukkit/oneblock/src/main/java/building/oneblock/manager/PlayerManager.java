package building.oneblock.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager implements Listener {
    private SpawnManager spawnManager;

    public PlayerManager(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Location spawnLocation = spawnManager.getSpawnLocation();
        Player player = event.getPlayer();

        event.setJoinMessage("§7[§e+§7] " + event.getPlayer().getName());

        if (!player.hasPlayedBefore()) {
            player.teleport(spawnLocation);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("§7[§e-§7] " + event.getPlayer().getName());
    }
}
