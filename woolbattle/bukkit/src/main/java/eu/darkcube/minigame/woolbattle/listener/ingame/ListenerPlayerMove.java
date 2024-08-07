/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.game.ingame.SchedulerHeightDisplay;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.BoundingBox;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerPlayerMove extends Listener<PlayerMoveEvent> {

    private final WoolBattleBukkit woolbattle;
    public Map<Player, Integer> ghostBlockFixCount = new HashMap<>();

    public ListenerPlayerMove(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @SuppressWarnings("deprecation")
    @Override
    @EventHandler
    public void handle(PlayerMoveEvent e) {
        SchedulerHeightDisplay.display(woolbattle, WBUser.getUser(e.getPlayer()));

        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        Player p = e.getPlayer();
        Collection<Location> blocksToCheck = new HashSet<>();

        BoundingBox pbox = new BoundingBox(p, e.getTo());
        Location to = e.getTo();
        Collection<Block> ignoreTeleport = new HashSet<>();
        boolean flawless = true;
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                for (int y = -1; y < 3; y++) {
                    Block r = to.clone().add(x, y, z).getBlock();
                    if (r.getType() == Material.AIR) {
                        continue;
                    }
                    BoundingBox box = new BoundingBox(r).shrink(0.2, 0, 0.2);
                    if (box.collides(pbox)) {
                        p.sendBlockChange(r.getLocation(), r.getType(), r.getData());
                    }
                    if (box.collides(pbox) && (box.collidesVertically(pbox) || r.getType() != Material.WOOL)) {
                        if (to.getY() % 1 != 0 && r.getLocation().getBlockY() == to.getBlockY()) {
                            flawless = false;
                        }
                        if (this.ghostBlockFixCount.getOrDefault(p, 0) > 5) {
                            blocksToCheck.add(r.getLocation());
                        }
                    }
                }
            }
        }
        if (flawless || e.getFrom().getY() < e.getTo().getY()) {
            this.ghostBlockFixCount.remove(p);
        } else {
            this.ghostBlockFixCount.put(p, this.ghostBlockFixCount.getOrDefault(p, 0) + 1);
        }

        Collection<Block> packets = new ArrayList<>();
        for (Location loc : blocksToCheck) {
            packets.add(loc.getBlock());
        }

        if (!packets.isEmpty()) {
            double y = -1;
            for (Block b : packets) {
                if (!ignoreTeleport.contains(b)) {
                    BoundingBox box = new BoundingBox(b);
                    y = Math.max(y, box.box.e);
                }
            }
            if (y != -1) {
                Location loc = new Location(to.getWorld(), to.getX(), y, to.getZ(), to.getYaw(), to.getPitch());
                p.teleport(loc);
            }
        }
    }

}
