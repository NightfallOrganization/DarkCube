package eu.darkcube.minigame.woolbattle.common.perk.perks.other;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class EnderPearlPerk extends Perk {
    public static final PerkName ENDERPEARL = new PerkName("ENDERPEARL");

    public EnderPearlPerk(CommonGame game) {
        super(ActivationType.MISC, ENDERPEARL, 5, 8, Items.DEFAULT_PEARL, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.DEFAULT_PEARL_COOLDOWN, g));
    }
}
