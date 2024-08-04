package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class FreezerPerk extends Perk {
    public static final PerkName FREEZER = new PerkName("FREEZER");

    public FreezerPerk(CommonGame game) {
        super(ActivationType.ACTIVE, FREEZER, 6, 6, Items.PERK_FREEZER, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_FREEZER_COOLDOWN, g));
    }
}
