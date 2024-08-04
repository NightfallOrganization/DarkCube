package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class LongJumpPerk extends Perk {
    public static final PerkName LONG_JUMP = new PerkName("LONG_JUMP");

    public LongJumpPerk(CommonGame game) {
        super(ActivationType.PASSIVE, LONG_JUMP, 0, 0, Items.PERK_LONGJUMP, (user, perk, id, perkSlot, g) -> new DefaultUserPerk(user, id, perkSlot, perk, g));
    }
}
