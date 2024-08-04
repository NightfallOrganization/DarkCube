package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public class BerserkerPerk extends Perk {
    public static final PerkName BERSERKER = new PerkName("BERSERKER");

    public BerserkerPerk(CommonGame game) {
        super(ActivationType.PASSIVE, BERSERKER, new Cooldown(Cooldown.Unit.ACTIVATIONS, 0), 0, Items.PERK_BERSERKER, BerserkerUserPerk::new);
    }

    private static class BerserkerUserPerk extends DefaultUserPerk {
        private final Key combo;

        public BerserkerUserPerk(WBUser owner, Perk perk, int id, int perkSlot, Game game) {
            super(owner, perk, id, perkSlot, game);
            combo = Key.key(game.woolbattle(), "perk_berserker_combo");
        }

        private int getHits() {
            return owner().user().metadata().getOr(combo, 0);
        }

        @Override
        public @NotNull PerkItem currentPerkItem() {
            return new PerkItem(this::currentItem, this) {
                @Override
                protected void modify(ItemBuilder item) {
                    item.glow(!item.glow());
                }

                @Override
                protected int itemAmount() {
                    return getHits();
                }
            };
        }
    }
}