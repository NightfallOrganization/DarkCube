package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class GrapplingHookPerk extends Perk {
    public static final PerkName GRAPPLING_HOOK = new PerkName("GRAPPLING_HOOK");

    public GrapplingHookPerk(CommonGame game) {
        super(ActivationType.ACTIVE, GRAPPLING_HOOK, 12, 12, Items.PERK_GRAPPLING_HOOK, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_GRAPPLING_HOOK_COOLDOWN, g));
    }
}
