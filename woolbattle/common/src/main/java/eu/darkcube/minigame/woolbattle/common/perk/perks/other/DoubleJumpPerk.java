package eu.darkcube.minigame.woolbattle.common.perk.perks.other;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;

public class DoubleJumpPerk extends Perk {
    public static final PerkName DOUBLE_JUMP = new PerkName("DOUBLE_JUMP");

    public DoubleJumpPerk(CommonGame game) {
        super(ActivationType.DOUBLE_JUMP, DOUBLE_JUMP, new Cooldown(Cooldown.Unit.TICKS, 63), 5, null, DoubleJumpUserPerk::new);
    }

    public static class DoubleJumpUserPerk extends DefaultUserPerk {

        public DoubleJumpUserPerk(WBUser owner, Perk perk, int id, int perkSlot, Game game) {
            super(owner, perk, id, perkSlot, game);
        }

        @Override
        public void cooldown(int cooldown) {
            // float max = perk().cooldown().cooldown();
            // owner().getBukkitEntity().setFoodLevel(7 + (Math.round((max - (float) cooldown) / max * 13)));
            // if (cooldown <= 0 && cooldown() > 0) {
            //     if (!owner().getBukkitEntity().getAllowFlight()) owner().getBukkitEntity().setAllowFlight(true);
            // }
            super.cooldown(cooldown);
        }
    }
}
