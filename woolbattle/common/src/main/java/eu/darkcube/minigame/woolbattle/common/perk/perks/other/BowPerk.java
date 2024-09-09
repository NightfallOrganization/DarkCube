package eu.darkcube.minigame.woolbattle.common.perk.perks.other;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class BowPerk extends Perk {
    public static final PerkName BOW = new PerkName("BOW");

    public BowPerk(CommonGame game) {
        super(ActivationType.PRIMARY_WEAPON, BOW, 0, 1, Items.DEFAULT_BOW, DefaultUserPerk::new);
    }
}
