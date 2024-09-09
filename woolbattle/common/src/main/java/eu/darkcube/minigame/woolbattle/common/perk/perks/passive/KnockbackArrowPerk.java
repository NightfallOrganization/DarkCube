package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class KnockbackArrowPerk extends Perk {
    public static final PerkName KNOCKBACK_ARROW = new PerkName("KNOCKBACK_ARROW");

    public KnockbackArrowPerk(CommonGame game) {
        super(ActivationType.PASSIVE, KNOCKBACK_ARROW, 0, 0, Items.PERK_KNOCKBACK_ARROW, DefaultUserPerk::new);
    }
}
