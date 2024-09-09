package eu.darkcube.minigame.woolbattle.common.perk.perks.other;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class ShearsPerk extends Perk {
    public static final PerkName SHEARS = new PerkName("SHEARS");

    public ShearsPerk(CommonGame game) {
        super(ActivationType.SECONDARY_WEAPON, SHEARS, 0, 0, Items.DEFAULT_SHEARS, DefaultUserPerk::new);
    }
}
