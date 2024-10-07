/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk.perks.other;

import java.util.concurrent.ThreadLocalRandom;

import eu.darkcube.minigame.woolbattle.api.entity.Arrow;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitBlockEvent;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitEntityEvent;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.listener.perks.PerkListener;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskSchedule;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.sound.Sound;

public class ArrowPerk extends Perk {
    public static final PerkName ARROW = new PerkName("ARROW");

    public ArrowPerk(Game game) {
        super(ActivationType.ARROW, ARROW, 0, 0, Items.DEFAULT_ARROW);
        addListeners(new ArrowPerkListener(game, this));
    }

    public static void particles(Game game, Arrow arrow, boolean particles) {
        arrow.metadata().set(particles(game), particles);
    }

    public static void claimArrow(Game game, WBUser user, Arrow arrow, float strength, int blockDamage) {
        arrow.metadata().set(user(game), user);
        arrow.metadata().set(perk(game), ARROW);
        arrow.metadata().set(strength(game), strength);
        arrow.metadata().set(blockDamage(game), blockDamage);
    }

    private static class ArrowPerkListener extends PerkListener {
        public ArrowPerkListener(Game game, Perk perk) {
            super(game, perk);
            listeners.addListener(ProjectileHitEvent.class, this::handle);
        }

        private void handle(ProjectileHitEvent event) {
            var projectile = event.entity();
            if (!projectile.metadata().has(ArrowPerk.perk(game))) return;
            var o = projectile.metadata().get(ArrowPerk.perk(game));
            if (!(o instanceof PerkName perkName)) return;
            if (!perkName.equals(perk.perkName())) return;
            var location = projectile.location();
            if (event instanceof ProjectileHitBlockEvent hitBlockEvent) {
                var block = hitBlockEvent.block();
                var damage = (int) projectile.metadata().get(blockDamage(game));
                if (game.api().materialProvider().isWool(block.material())) {
                    block.incrementBlockDamage(damage);
                }
            } else if (event instanceof ProjectileHitEntityEvent hitEntityEvent) {
                var shooter = projectile.shooter();
                if (shooter != null) {
                    shooter.playSound(Sound.sound(Key.key("minecraft:entity.arrow.hit_player"), Sound.Source.PLAYER, 1, 0));
                }
                var target = hitEntityEvent.target();
                if (target instanceof WBUser targetUser) {
                    if (shooter != null) {
                        shooter.attack(targetUser);
                    }
                    var strength = projectile.metadata().<Float>getOr(strength(game), 0F);
                    var projectileVelocity = projectile.velocity();
                    var velocity = (projectileVelocity.lengthSquared() == 0.0 ? projectileVelocity : projectileVelocity.withY(0).normalized().mul(.47 + ThreadLocalRandom.current().nextDouble() / 70 + strength / 1.42)).withY(.400023);
                    target.velocity(velocity);
                }
                // TODO block damage around player
            }
            game.scheduler().schedule(projectile::remove, TaskSchedule.nextTick(), TaskSchedule.stop());
            location.world().playSound(location, Sound.sound(Key.key("minecraft:entity.arrow.hit"), Sound.Source.PLAYER, 1, 1));
        }
    }

    private static Key particles(Game game) {
        return Key.key(game.api(), "particles");
    }

    private static Key user(Game game) {
        return Key.key(game.api(), "user");
    }

    private static Key perk(Game game) {
        return Key.key(game.api(), "perk");
    }

    private static Key strength(Game game) {
        return Key.key(game.api(), "strength");
    }

    private static Key blockDamage(Game game) {
        return Key.key(game.api(), "block_damage");
    }
}
