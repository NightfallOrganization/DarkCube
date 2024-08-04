package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class PiercingArrowPerk extends Perk {
    public static final PerkName PIERCING_ARROW = new PerkName("PIERCING_ARROW");

    public PiercingArrowPerk(CommonGame game) {
        super(ActivationType.PASSIVE, PIERCING_ARROW, 0, 0, Items.PERK_PIERCING_ARROW, DefaultUserPerk::new);
    }
}
