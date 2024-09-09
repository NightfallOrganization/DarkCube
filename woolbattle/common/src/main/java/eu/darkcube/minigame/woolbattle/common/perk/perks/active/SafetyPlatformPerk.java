package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class SafetyPlatformPerk extends Perk {
    public static final PerkName SAFETY_PLATFORM = new PerkName("SAFETY_PLATFORM");

    public SafetyPlatformPerk(CommonGame game) {
        super(ActivationType.ACTIVE, SAFETY_PLATFORM, 25, 24, Items.PERK_SAFETY_PLATFORM, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_SAFETY_PLATFORM_COOLDOWN, game));
    }
}
