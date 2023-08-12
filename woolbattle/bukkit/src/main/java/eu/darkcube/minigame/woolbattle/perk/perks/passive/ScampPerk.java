/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.event.perk.other.PlayerHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScampPerk extends Perk {
    public static final PerkName SCAMP = new PerkName("SCAMP");

    public ScampPerk() {
        super(ActivationType.PASSIVE, SCAMP, 0, 2, Item.PERK_SCAMP, DefaultUserPerk::new);
        addListener(new ScampListener());
    }

    public class ScampListener implements Listener {
        @EventHandler public void handle(PlayerHitPlayerEvent event) {
            WBUser attacker = event.attacker();
            WBUser target = event.target();
            for (UserPerk perk : attacker.perks().perks(perkName())) {
                scamp(perk, target);
            }
        }

        private void scamp(UserPerk perk, WBUser target) {
            if (perk.owner().woolCount() >= perk.perk().cost() && target.woolCount() >= 1) {
                perk.owner().removeWool(perk.perk().cost());
                perk.owner().addWool(target.removeWool(7));
            }
        }
    }
}
