/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk.perks.other;

import static eu.darkcube.minigame.woolbattle.api.util.PerkUtils.playSoundNotEnoughWool;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import eu.darkcube.minigame.woolbattle.api.event.perk.BowShootArrowEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserShootBowEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.listener.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.api.util.PerkUtils;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.sound.Sound;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class BowPerk extends Perk {
    public static final PerkName BOW = new PerkName("BOW");

    public BowPerk(CommonGame game) {
        super(ActivationType.PRIMARY_WEAPON, BOW, 0, 1, Items.DEFAULT_BOW, DefaultUserPerk::new);
        addListeners(new BowListener(game, this));
    }

    private static class BowListener extends BasicPerkListener {
        public BowListener(Game game, Perk perk) {
            super(game, perk);
            this.listeners.addListener(UserShootBowEvent.class, this::handle);
        }

        protected boolean activate(@NotNull UserPerk perk, float power) {
            var user = perk.owner();
            var eyeLocation = user.eyeLocation();
            if (eyeLocation == null) return false;
            var arrow = game.api().entityImplementations().shootArrow(user, eyeLocation, power * 3F, 1F);
            ArrowPerk.claimArrow(game, user, arrow, 2, 1);
            eyeLocation.world().playSound(eyeLocation, Sound.sound(Key.key("minecraft:entity.arrow.shoot"), Sound.Source.PLAYER, 1, getRandomPitchFromPower(power)));
            game.api().eventManager().call(new BowShootArrowEvent(user, arrow));
            return true;
        }

        @Override
        protected boolean mayActivate() {
            return false; // We manually call #activate
        }

        private void handle(UserShootBowEvent event) {
            var user = event.user();
            var item = event.item();
            var checkResult = PerkUtils.checkUsable(user, item, perk, game);
            var userPerk = checkResult.userPerk();
            var usability = checkResult.usability();
            if (!usability.booleanValue()) {
                if (usability == PerkUtils.Usability.NOT_ENOUGH_WOOL || usability == PerkUtils.Usability.ON_COOLDOWN) {
                    playSoundNotEnoughWool(user);
                }
                return;
            }
            Objects.requireNonNull(userPerk);

            if (!activate(userPerk, event.power())) {
                return;
            }
            activated(userPerk);
        }

        private static float getRandomPitchFromPower(double power) {
            return (float) (1F / (ThreadLocalRandom.current().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
        }
    }
}
