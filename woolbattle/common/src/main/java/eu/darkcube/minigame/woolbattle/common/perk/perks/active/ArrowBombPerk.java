/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import java.util.concurrent.ThreadLocalRandom;

import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.event.entity.EntityDamageByEntityEvent;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.listener.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.ArrowPerk;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ArrowBombPerk extends Perk {
    public static final PerkName ARROW_BOMB = new PerkName("ARROW_BOMB");

    public ArrowBombPerk(@NotNull Game game) {
        super(ActivationType.ACTIVE, ARROW_BOMB, 9, 7, Items.PERK_ARROW_BOMB, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_ARROW_BOMB_COOLDOWN, g));
        addListeners(new ArrowBombListener(game, this));
    }

    private static class ArrowBombListener extends BasicPerkListener {
        public ArrowBombListener(Game game, Perk perk) {
            super(game, perk);
            listeners.addListener(ProjectileHitEvent.class, this::handle);
            listeners.addListener(EntityDamageByEntityEvent.class, this::handle);
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            var snowball = game.woolbattle().entityImplementations().launchSnowball(perk.owner());
            snowball.metadata().set(perkKey, perk);
            return true;
        }

        private void handle(ProjectileHitEvent event) {
            var projectile = event.entity();
            var o = projectile.metadata().get(perkKey);
            if (o == null) return;
            if (!(o instanceof UserPerk perk)) return;
            var user = perk.owner();
            var random = ThreadLocalRandom.current();
            var count = 30;
            for (var i = 0; i < count; i++) {
                var pitch = random.nextFloat() * 50 + 10;
                var yaw = random.nextInt(360 * 2) / 2F;
                if (random.nextFloat() < 0.1) {
                    pitch = pitch - 10 + random.nextFloat() * 40;
                } else {
                    pitch = -pitch;
                }
                var dir = Vector.fromEuler(yaw, pitch);
                var arrow = game.woolbattle().entityImplementations().spawnArrow(projectile.location(), dir, 0.9F, 0);
                ArrowPerk.particles(game, arrow, false);
                ArrowPerk.claimArrow(game, user, arrow, 3, 2);
            }
        }

        private void handle(EntityDamageByEntityEvent event) {
            if (event.damager() instanceof Projectile projectile) {
                if (!projectile.metadata().has(perkKey)) return;
                var o = projectile.metadata().get(perkKey);
                if (!perk.perkName().equals(o)) return;
                event.cancel();
            }
        }
    }
}
