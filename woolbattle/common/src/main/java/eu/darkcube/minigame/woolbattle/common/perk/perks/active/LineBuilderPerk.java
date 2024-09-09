package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class LineBuilderPerk extends Perk {
    public static final PerkName LINE_BUILDER = new PerkName("LINE_BUILDER");

    public LineBuilderPerk(CommonGame game) {
        super(ActivationType.ACTIVE, LINE_BUILDER, new Cooldown(Cooldown.Unit.TICKS, 10 * 20), true, 2, Items.PERK_LINE_BUILDER, (user, perk, id, perkSlot, g) -> new LineBuilderUserPerk(user, id, perkSlot, perk, g));
    }

    public static class LineBuilderUserPerk extends CooldownUserPerk {

        private boolean useCooldownItem = false;

        public LineBuilderUserPerk(WBUser owner, int id, int perkSlot, Perk perk, Game game) {
            super(owner, id, perkSlot, perk, Items.PERK_LINE_BUILDER_COOLDOWN, game);
        }

        @Override
        public boolean useCooldownItem() {
            return useCooldownItem;
        }

        @Override
        public void cooldown(int cooldown) {
            super.cooldown(cooldown);
            if (cooldown() >= perk().cooldown().cooldown()) {
                useCooldownItem = true;
            } else if (cooldown == 0) {
                useCooldownItem = false;
            }
        }
    }
}
