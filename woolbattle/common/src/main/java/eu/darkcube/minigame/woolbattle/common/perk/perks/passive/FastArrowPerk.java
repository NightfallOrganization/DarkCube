package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class FastArrowPerk extends Perk {
    public static final PerkName FAST_ARROW = new PerkName("FAST_ARROW");

    public FastArrowPerk(CommonGame game) {
        super(ActivationType.PASSIVE, FAST_ARROW, 0, 0, Items.PERK_FAST_ARROW, (user, perk, id, perkSlot, g) -> new DefaultUserPerk(user, id, perkSlot, perk, g));
    }
}
