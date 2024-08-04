package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class RocketJumpPerk extends Perk {
    public static final PerkName ROCKET_JUMP = new PerkName("ROCKET_JUMP");

    public RocketJumpPerk(CommonGame game) {
        super(ActivationType.PASSIVE, ROCKET_JUMP, 0, 0, Items.PERK_ROCKETJUMP, (user, perk, id, perkSlot, g) -> new DefaultUserPerk(user, id, perkSlot, perk, g));
    }
}
