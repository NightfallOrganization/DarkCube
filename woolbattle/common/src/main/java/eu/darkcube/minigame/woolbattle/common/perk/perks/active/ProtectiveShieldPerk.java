package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class ProtectiveShieldPerk extends Perk {
    public static final PerkName PROTECTIVE_SHIELD = new PerkName("PROTECTIVE_SHIELD");

    public ProtectiveShieldPerk(CommonGame game) {
        super(ActivationType.ACTIVE, PROTECTIVE_SHIELD, 15, 5, Items.PERK_PROTECTIVE_SHIELD, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_PROTECTIVE_SHIELD_COOLDOWN, g));
    }
}
