/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.event.perk.other.BowShootArrowEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class ArrowRainPerk extends Perk {
    public static final PerkName ARROW_RAIN = new PerkName("ARROW_RAIN");

    public ArrowRainPerk() {
        super(ActivationType.PASSIVE, ARROW_RAIN, new Cooldown(Unit.ACTIVATIONS, 6), false, 0, Item.PERK_ARROW_RAIN, (user, perk, id, perkSlot, woolbattle) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_ARROW_RAIN_COOLDOWN, woolbattle));
        addListener(new ArrowRainListener());
    }

    private static class ArrowRainListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void handle(BowShootArrowEvent event) {
            int arrows = 0;
            for (UserPerk perk : event.user().perks().perks(ARROW_RAIN)) {
                if (perk.cooldown() > 0) {
                    perk.cooldown(perk.cooldown() - 1);
                    continue;
                }
                if (event.user().removeWool(perk.perk().cost()) != perk.perk().cost()) continue;
                perk.cooldown(perk.perk().cooldown().cooldown());
                arrows += 6;
            }

            this.shootArrows(event.user(), 8, arrows / 2, event.arrow());
            this.shootArrows(event.user(), 8, arrows / 2, event.arrow());
        }

        private void shootArrows(WBUser user, @SuppressWarnings("SameParameterValue") float angle, int count, Arrow original) {
            Player p = user.getBukkitEntity();
            for (int i = 0; i < count; i++) {
                Arrow arrow = p.launchProjectile(Arrow.class);
                arrow.setKnockbackStrength(2);
                Vector vec = original.getVelocity();
                Location l = new Location(null, 0, 0, 0);
                l.setDirection(vec);
                l.setYaw(l.getYaw() + (i + 1) * angle);
                vec = l.getDirection();
                vec.multiply(original.getVelocity().length());
                arrow.setVelocity(vec);
            }
        }
    }
}
