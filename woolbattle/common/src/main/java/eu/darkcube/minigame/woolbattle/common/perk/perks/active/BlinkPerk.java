package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class BlinkPerk extends Perk {
    public static final PerkName BLINK = new PerkName("BLINK");

    public BlinkPerk(CommonGame game) {
        super(ActivationType.ACTIVE, BLINK, 15, 12, Items.PERK_BLINK, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_BLINK_COOLDOWN, g));
    }
}
