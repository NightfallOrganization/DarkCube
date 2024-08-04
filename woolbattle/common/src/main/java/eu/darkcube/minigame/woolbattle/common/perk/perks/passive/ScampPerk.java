package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class ScampPerk extends Perk {
    public static final PerkName SCAMP = new PerkName("SCAMP");

    public ScampPerk(CommonGame game) {
        super(ActivationType.PASSIVE, SCAMP, 0, 2, Items.PERK_SCAMP, DefaultUserPerk::new);
    }
}
