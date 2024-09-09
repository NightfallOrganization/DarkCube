package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class PodPerk extends Perk {
    public static final PerkName POD = new PerkName("POD");

    public PodPerk(CommonGame game) {
        super(ActivationType.ACTIVE, POD, 15, 15, Items.PERK_POD, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_POD_COOLDOWN, g));
    }
}
