package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class ReflectorPerk extends Perk {
    public static final PerkName REFLECTOR = new PerkName("REFLECTOR");

    public ReflectorPerk(CommonGame game) {
        super(ActivationType.PASSIVE, REFLECTOR, 70, 15, Items.PERK_REFLECTOR, DefaultUserPerk::new);
    }
}
