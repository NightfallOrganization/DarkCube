package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class SlimePlatformPerk extends Perk {
    public static final PerkName SLIME_PLATFORM = new PerkName("SLIME_PLATFORM");

    public SlimePlatformPerk(CommonGame game) {
        super(ActivationType.ACTIVE, SLIME_PLATFORM, 20, 18, Items.PERK_SLIME_PLATFORM, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_SLIME_PLATFORM_COOLDOWN, g));
    }
}
