package eu.darkcube.system.citybuild.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinHealthSetupListener implements Listener {

    private final CustomHealthManager healthManager;

    public PlayerJoinHealthSetupListener(CustomHealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (healthManager.getHealth(player) == 0) {
            healthManager.setHealth(player, 20);
        }
        if (healthManager.getMaxHealth(player) == 0) {
            healthManager.setMaxHealth(player, 20);
        }
    }
}
