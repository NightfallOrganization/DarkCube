package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class GrabberPerk extends Perk {
    public static final PerkName GRABBER = new PerkName("GRABBER");

    public GrabberPerk(CommonGame game) {
        super(ActivationType.ACTIVE, GRABBER, 7, 10, Items.PERK_GRABBER, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_GRABBER_COOLDOWN, g));
    }
}
