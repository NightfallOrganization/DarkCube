/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.gadget.listener;

import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.listener.BaseListener;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class ListenerGrapplingHook extends BaseListener {
    private static Collection<UUID> waitingForFlight = new HashSet<>();

    @EventHandler public void handle(PlayerFishEvent e) {
        State state = e.getState();
        if (state != State.CAUGHT_FISH && state != State.FISHING) {
            Player p = e.getPlayer();
            LobbyUser user = UserWrapper.fromUser(UserAPI.instance().user(p.getUniqueId()));
            FishHook hook = e.getHook();
            if (user.getGadget() == Gadget.GRAPPLING_HOOK) {

                Location l = p.getEyeLocation();
                Location to = hook.getLocation();

                double d = Math.sqrt(l.getBlock().getLocation().distance(to.getBlock().getLocation())) / 10;

                if (state == State.IN_GROUND) {
                    d *= 1.5;
                }

                double posneg = (to.getY() - l.getY() + 0.3) / Math.abs((to.getY() - l.getY() + 0.3));

                double x = (to.getX() - l.getX()) * d / 3;
                // double y = posneg * Math.sqrt(Math.abs((to.getY() - l.getY()) * d)) * 2 *
                // Math.sqrt(d);
                double y = posneg * (to.getY() - l.getY()) * d / 3;
                double z = (to.getZ() - l.getZ()) * d / 3;

                y = y * y / 20 + 0.5;

                p.setAllowFlight(false);
                // ((CraftPlayer) p).getHandle().abilities.canFly = true;
                ListenerGrapplingHook.waitingForFlight.add(p.getUniqueId());

                // p.setVelocity(new Vector(0, posneg * 0.3, 0));
                final double fy = y;
                // Bukkit.getScheduler().runTaskLater(Lobby.getInstance(), new Runnable() {
                // @Override
                // public void run() {
                Vector v = new Vector(x, fy, z).multiply(1.4);
                if (v.length() >= 3.9) {
                    v.normalize().multiply(3.9);
                }
                p.setVelocity(v);
                // }
                // }, 2);
            }
        }
    }

    @SuppressWarnings("deprecation") @EventHandler public void handle(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        LobbyUser user = UserWrapper.fromUser(UserAPI.instance().user(p.getUniqueId()));
        if (!user.isBuildMode() && ListenerGrapplingHook.waitingForFlight.contains(p.getUniqueId()) && p.isOnGround()) {
            ListenerGrapplingHook.waitingForFlight.remove(p.getUniqueId());
            p.setAllowFlight(true);
        }
    }
}
