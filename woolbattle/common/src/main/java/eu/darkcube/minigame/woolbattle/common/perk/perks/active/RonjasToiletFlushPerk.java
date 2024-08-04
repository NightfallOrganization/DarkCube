package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class RonjasToiletFlushPerk extends Perk {
    public static final PerkName RONJAS_TOILET_FLUSH = new PerkName("RONJAS_TOILET_FLUSH");

    public RonjasToiletFlushPerk(CommonGame game) {
        super(ActivationType.ACTIVE, RONJAS_TOILET_FLUSH, 13, 12, Items.PERK_RONJAS_TOILET_SPLASH, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_RONJAS_TOILET_SPLASH_COOLDOWN, g));
    }
}
