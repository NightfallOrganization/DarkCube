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
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BoosterPerk extends Perk {
    public static final PerkName BOOSTER = new PerkName("BOOSTER");

    public BoosterPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, BOOSTER, 18, 12, Item.PERK_BOOSTER, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_BOOSTER_COOLDOWN, woolbattle));
        addListener(new ListenerBooster(this, woolbattle));
    }

    public static class ListenerBooster extends BasicPerkListener {

        public ListenerBooster(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            Player p = perk.owner().getBukkitEntity();
            Vector velo = p.getLocation().getDirection().setY(p.getLocation().getDirection().getY() + 0.3).multiply(2.7);
            velo.setY(velo.getY() / 1.8);
            p.setVelocity(velo);
            return true;
        }
    }
}
