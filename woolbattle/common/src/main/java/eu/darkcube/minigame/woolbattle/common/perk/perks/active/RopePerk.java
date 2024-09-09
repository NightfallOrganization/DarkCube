package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class RopePerk extends Perk {
    public static final PerkName ROPE = new PerkName("ROPE");

    public RopePerk(CommonGame game) {
        super(ActivationType.ACTIVE, ROPE, 12, 12, Items.PERK_ROPE, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_ROPE_COOLDOWN, g));
    }
}
