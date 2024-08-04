package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class FreezeArrowPerk extends Perk {
    public static final PerkName FREEZE_ARROW = new PerkName("FREEZE_ARROW");

    public FreezeArrowPerk(CommonGame game) {
        super(ActivationType.PASSIVE, FREEZE_ARROW, new Cooldown(Cooldown.Unit.ACTIVATIONS, 3), false, 4, Items.PERK_FREEZE_ARROW, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_FREEZE_ARROW_COOLDOWN, wb));
    }
}
