/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import java.util.Random;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowArrowHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.event.perk.other.PlayerHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReflectorPerk extends Perk {
    public static final PerkName REFLECTOR = new PerkName("REFLECTOR");

    public ReflectorPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.PASSIVE, REFLECTOR, 70, 15, Item.PERK_REFLECTOR, DefaultUserPerk::new);
        addListener(new ReflectorListener(woolbattle));
    }

    private final class ReflectorListener implements Listener {
        private final Random random = new Random();
        private final WoolBattleBukkit woolbattle;
        private final Key KEY_STORED_KNOCKBACK;

        public ReflectorListener(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
            KEY_STORED_KNOCKBACK = Key.key(woolbattle, "reflector_stored_knockback");
        }

        private boolean attack(WBUser user, WBUser target) {
            if (user.user().metadata().has(KEY_STORED_KNOCKBACK)) {
                if (random.nextDouble() < 0.5) {
                    double stored = user.user().metadata().get(KEY_STORED_KNOCKBACK);
                    var v = target.getBukkitEntity().getLocation().toVector().subtract(user.getBukkitEntity().getLocation().toVector());
                    v.normalize();
                    v.multiply(stored);
                    v.setY(.4500023);
                    if (woolbattle.ingame().playerUtil().attack(user, target)) {
                        user.user().metadata().remove(KEY_STORED_KNOCKBACK);
                        target.getBukkitEntity().damage(0);
                        target.getBukkitEntity().setVelocity(v);
                    }
                    return true;
                }
            }
            return false;
        }

        private void store(WBUser user) {
            for (var i = 0; i < user.perks().count(perkName()); i++) {
                if (user.user().metadata().has(KEY_STORED_KNOCKBACK)) {
                    double stored = user.user().metadata().get(KEY_STORED_KNOCKBACK);
                    var exp = 0.65;
                    stored = Math.pow(Math.pow(stored, 1 / (exp + 0.03)) + 15, exp);
                    user.user().metadata().set(KEY_STORED_KNOCKBACK, stored);
                } else {
                    user.user().metadata().set(KEY_STORED_KNOCKBACK, 5D);
                }
            }
        }

        @EventHandler
        public void handle(PlayerHitPlayerEvent event) {
            store(event.target());
            if (attack(event.attacker(), event.target())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void handle(BowArrowHitPlayerEvent event) {
            store(event.target());
            if (attack(event.shooter(), event.target())) {
                event.setCancelled(true);
            }
        }
    }
}
