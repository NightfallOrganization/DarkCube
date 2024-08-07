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
import org.bukkit.entity.Player;

public class PodPerk extends Perk {
    public static final PerkName POD = new PerkName("POD");

    public PodPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, POD, 15, 15, Item.PERK_POD, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_POD_COOLDOWN, woolbattle));
        addListener(new ListenerPod(this, woolbattle));
    }

    public static class ListenerPod extends BasicPerkListener {
        private final WoolBattleBukkit woolbattle;

        public ListenerPod(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
            this.woolbattle = woolbattle;
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            Player p = perk.owner().getBukkitEntity();
            Location loc = p.getLocation();
            this.setBlock(perk.owner(), loc.subtract(0, 1, 0));

            this.setBlock2(perk.owner(), loc.add(1, 1, 0));
            this.setBlock2(perk.owner(), loc.add(-1, 0, 1));
            this.setBlock2(perk.owner(), loc.add(-1, 0, -1));
            this.setBlock2(perk.owner(), loc.add(1, 0, -1));

            p.teleport(p.getLocation().getBlock().getLocation().add(.5, .25, .5).setDirection(p.getLocation().getDirection()));
            return true;
        }

        private void setBlock(WBUser user, Location block) {
            woolbattle.ingame().place(user, block.getBlock(), 2, false);
        }

        private void setBlock2(WBUser user, Location block) {
            woolbattle.ingame().place(user, block.getBlock(), 0, false);
        }
    }
}
