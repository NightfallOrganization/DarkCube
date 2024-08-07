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
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;

public class GrandpasClockPerk extends Perk {
    public static final PerkName GRANDPAS_CLOCK = new PerkName("GRANDPAS_CLOCK");

    public GrandpasClockPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, GRANDPAS_CLOCK, 14, 18, Item.PERK_GRANDPAS_CLOCK, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_GRANDPAS_CLOCK_COOLDOWN, woolbattle));
        addListener(new ListenerGrandpasClock(this, woolbattle));
    }

    public static class ListenerGrandpasClock extends BasicPerkListener {

        private final Key DATA_OLD_POS = Key.key(woolbattle, "grandpas_clock_old_pos");
        private final Key DATA_TICKER = Key.key(woolbattle, "grandpas_clock_ticker");

        public ListenerGrandpasClock(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
        }

        @Override
        protected boolean activate(UserPerk perk) {
            WBUser user = perk.owner();
            if (user.user().metadata().has(DATA_OLD_POS)) {
                user.getBukkitEntity().teleport(user.user().metadata().<Location>remove(DATA_OLD_POS));
                user.user().metadata().<Scheduler>remove(DATA_TICKER).cancel();
                user.getBukkitEntity().playSound(user.getBukkitEntity().getBedSpawnLocation(), Sound.ENDERMAN_TELEPORT, 100, 1);
                return true;
            }
            user.user().metadata().set(DATA_OLD_POS, user.getBukkitEntity().getLocation());
            user.user().metadata().set(DATA_TICKER, new Scheduler(woolbattle) {
                private int count = 0;

                {
                    runTaskTimer(10);
                }

                @Override
                public void run() {
                    if (count++ == 6) {
                        user.getBukkitEntity().teleport(user.user().metadata().<Location>remove(DATA_OLD_POS));
                        user.user().metadata().remove(DATA_TICKER);
                        user.getBukkitEntity().playSound(user.getBukkitEntity().getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                        cancel();
                        activated(perk);
                        return;
                    }
                    user.getBukkitEntity().playSound(user.getBukkitEntity().getLocation(), Sound.CLICK, 100, 1);
                }
            });
            return false;
        }
    }
}
