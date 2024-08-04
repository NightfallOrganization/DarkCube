package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class StomperPerk extends Perk {
    public static final PerkName STOMPER = new PerkName("STOMPER");

    public StomperPerk(CommonGame game) {
        super(ActivationType.PASSIVE, STOMPER, new Cooldown(Cooldown.Unit.ACTIVATIONS, 0), 10, Items.PERK_STOMPER, DefaultUserPerk::new);
    }
}
