package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class ExtraWoolPerk extends Perk {
    public static final PerkName EXTRA_WOOL = new PerkName("EXTRA_WOOL");

    public ExtraWoolPerk(CommonGame game) {
        super(ActivationType.PASSIVE, EXTRA_WOOL, 0, 0, Items.PERK_EXTRA_WOOL, (user, perk, id, perkSlot, g) -> new DefaultUserPerk(user, id, perkSlot, perk, g));
    }
}
