package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class GrandpasClockPerk extends Perk {
    public static final PerkName GRANDPAS_CLOCK = new PerkName("GRANDPAS_CLOCK");

    public GrandpasClockPerk(CommonGame game) {
        super(ActivationType.ACTIVE, GRANDPAS_CLOCK, 16, 18, Items.PERK_GRANDPAS_CLOCK, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_GRANDPAS_CLOCK_COOLDOWN, g));
    }
}
