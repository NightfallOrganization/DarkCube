package eu.darkcube.system.citybuild.commands;

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
        if (healthManager.getHealth(event.getPlayer()) == 0) {
            healthManager.setHealth(event.getPlayer(), 20);
        }
    }
}
