/*
 * Copyright (c) 2023-2024. [DarkCube]
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
import org.bukkit.Location;

public class SafetyPlatformPerk extends Perk {
    public static final PerkName SAFETY_PLATFORM = new PerkName("SAFETY_PLATFORM");

    public SafetyPlatformPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, SAFETY_PLATFORM, 25, 24, Item.PERK_SAFETY_PLATFORM, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_SAFETY_PLATFORM_COOLDOWN, woolbattle));
        addListener(new ListenerSafetyPlatform(this, woolbattle));
    }

    public static class ListenerSafetyPlatform extends BasicPerkListener {
        private final WoolBattleBukkit woolbattle;

        public ListenerSafetyPlatform(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
            this.woolbattle = woolbattle;
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            boolean suc = setBlocks(perk.owner());
            if (!suc) woolbattle.ingame().playSoundNotEnoughWool(perk.owner());
            return suc;
        }

        private boolean setBlocks(WBUser p) {

            final Location pLoc = p.getBukkitEntity().getLocation();
            Location center = pLoc.getBlock().getLocation().add(0.5, 0.25, 0.5).setDirection(pLoc.getDirection());
            center.subtract(-0.5, 0, -0.5);
            final double radius = 2.5;
            for (double x = -radius; x <= radius; x++) {
                for (double z = -radius; z <= radius; z++) {
                    if (isCorner(x, z, radius)) {
                        continue;
                    }
                    block(center.clone().add(x, -1, z), p);
                }
            }

            p.getBukkitEntity().teleport(center);
            return true;
        }

        private boolean isCorner(double x, double z, @SuppressWarnings("SameParameterValue") double r) {
            return (x == -r && z == -r) || (x == r && z == -r) || (x == -r && z == r) || (x == r && z == r);
        }

        private void block(Location loc, WBUser u) {
            woolbattle.ingame().place(u, loc.getBlock());
        }
    }

}
