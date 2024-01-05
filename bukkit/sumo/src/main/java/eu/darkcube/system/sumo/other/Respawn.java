/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import eu.darkcube.system.sumo.ruler.MainRuler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.Random;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class Respawn implements Listener {
    private MainRuler mainRuler;
    private Random random;

    public Respawn(MainRuler mainRuler) {
        this.mainRuler = mainRuler;
        this.random = new Random();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().equals(mainRuler.getActiveWorld()) && event.getTo().getY() < 80) {
            teleportPlayerRandomly(event.getPlayer());
        }
    }

    void teleportPlayerRandomly(Player player) {
        World world = mainRuler.getActiveWorld();
        int x = random.nextInt(13) - 6; // Zufällige X-Koordinate im Bereich von -6 bis +6
        int z = random.nextInt(13) - 6; // Zufällige Z-Koordinate im Bereich von -6 bis +6
        Location respawnLocation = new Location(world, x, 107, z, player.getLocation().getYaw(), 50);
        player.teleport(respawnLocation);
    }
}
