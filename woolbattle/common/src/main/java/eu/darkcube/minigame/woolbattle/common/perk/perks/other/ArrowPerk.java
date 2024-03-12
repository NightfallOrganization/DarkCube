/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk.perks.other;

import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitBlockEvent;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.listener.perks.PerkListener;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.util.data.Key;

public class ArrowPerk extends Perk {
    public static final PerkName ARROW = new PerkName("ARROW");

    public ArrowPerk(Game game) {
        super(ActivationType.ARROW, ARROW, 0, 0, Items.DEFAULT_ARROW);
        addListeners(new ArrowPerkListener(game, this));
    }

    public static void particles(Game game, Projectile arrow, boolean particles) {
        arrow.metadata().set(particles(game), particles);
    }

    public static void claimArrow(Game game, WBUser user, Projectile arrow, float strength, float blockDamage) {
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
            if (event instanceof ProjectileHitBlockEvent hitBlockEvent) {
                var block = hitBlockEvent.block();
                var damage = (int) projectile.metadata().get(blockDamage(game));
                if (game.woolbattle().materialProvider().isWool(block.material())) {
                    block.incrementBlockDamage(damage);
                }
            }
            projectile.remove();
        }
    }

    private static Key particles(Game game) {
        return new Key(game.woolbattle(), "particles");
    }

    private static Key user(Game game) {
        return new Key(game.woolbattle(), "user");
    }

    private static Key perk(Game game) {
        return new Key(game.woolbattle(), "perk");
    }

    private static Key strength(Game game) {
        return new Key(game.woolbattle(), "strength");
    }

    private static Key blockDamage(Game game) {
        return new Key(game.woolbattle(), "blockDamage");
    }
}
