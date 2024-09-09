package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class TntArrowPerk extends Perk {
    public static final PerkName TNT_ARROW = new PerkName("TNT_ARROW");

    public TntArrowPerk(CommonGame game) {
        super(ActivationType.PASSIVE, TNT_ARROW, new Cooldown(Cooldown.Unit.ACTIVATIONS, 7), false, 16, Items.PERK_TNT_ARROW, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_TNT_ARROW_COOLDOWN, wb));
    }
}
