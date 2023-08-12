/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.RayTrace;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class BlinkPerk extends Perk {
    public static final PerkName BLINK = new PerkName("BLINK");

    public BlinkPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, BLINK, 15, 12, Item.PERK_BLINK, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_BLINK_COOLDOWN, woolbattle));
        addListener(new ListenerBlink(this, woolbattle));
    }

    public static class ListenerBlink extends BasicPerkListener {

        public ListenerBlink(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
        }

        private static boolean teleport(WBUser user) {
            Player p = user.getBukkitEntity();
            int dist = 15;
            if (p.isSneaking()) {
                RayTrace raytrace = new RayTrace(p.getEyeLocation().toVector(), p.getEyeLocation().getDirection());
                List<Vector> positions = raytrace.traverse(dist, 0.1);
                for (Vector position : positions) {
                    Location pos = position.toLocation(p.getWorld());
                    if (!ListenerBlink.checkBlock(pos)) {
                        if (ListenerBlink.checkBlock(pos.add(0, 1, 0)) && ListenerBlink.checkBlock(pos.add(0, 1, 0))) {
                            pos.subtract(0, 2, 0).setDirection(p.getEyeLocation().getDirection());
                            pos.setY(pos.getBlockY() + 1.01);
                            p.teleport(pos);
                            return true;
                        }
                    }
                }
            }
            Vector v = user.getBukkitEntity().getEyeLocation().getDirection().normalize();
            for (; dist > 1; dist--) {
                Location loc = user.getBukkitEntity().getEyeLocation().add(v.clone().multiply(dist));
                Location loc2 = loc.clone().add(0, 1, 0);
                if (loc.getBlock().getType() == Material.AIR && loc2.getBlock().getType() == Material.AIR) {
                    boolean freeUp = ListenerBlink.checkLayer(loc2.clone().add(0, 1, 0));
                    boolean freeHead = ListenerBlink.checkLayer(loc2);
                    boolean freeFoot = ListenerBlink.checkLayer(loc);
                    boolean freeDown = ListenerBlink.checkLayer(loc.clone().subtract(0, 1, 0));

                    boolean free = freeUp && freeHead && freeFoot && freeDown;
                    if (free) {
                        user.getBukkitEntity().teleport(loc.setDirection(v));
                    } else {
                        user.getBukkitEntity().teleport(loc.getBlock().getLocation().add(.5, .1, .5).setDirection(v));
                    }
                    return true;
                }
            }
            return false;
        }

        private static boolean checkBlock(Location block) {
            return block.getBlock().getType() == Material.AIR;
        }

        private static boolean checkLayer(Location layer) {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    if (layer.clone().add(x, 0, z).getBlock().getType() != Material.AIR) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override protected boolean activateRight(UserPerk perk) {
            return ListenerBlink.teleport(perk.owner());
        }

    }

}
