package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class SwitcherPerk extends Perk {
    public static final PerkName SWITCHER = new PerkName("SWITCHER");

    public SwitcherPerk(CommonGame game) {
        super(ActivationType.ACTIVE, SWITCHER, 7, 8, Items.PERK_SWITCHER, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_SWITCHER_COOLDOWN, g));
    }
}
