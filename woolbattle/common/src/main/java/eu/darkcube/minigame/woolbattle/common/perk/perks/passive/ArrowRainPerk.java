package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class ArrowRainPerk extends Perk {
    public static final PerkName ARROW_RAIN = new PerkName("ARROW_RAIN");

    public ArrowRainPerk(CommonGame game) {
        super(ActivationType.PASSIVE, ARROW_RAIN, new Cooldown(Cooldown.Unit.ACTIVATIONS, 6), false, 0, Items.PERK_ARROW_RAIN, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_ARROW_RAIN_COOLDOWN, g));
    }
}
