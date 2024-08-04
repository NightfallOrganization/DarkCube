package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class HookArrowPerk extends Perk {
    public static final PerkName HOOK_ARROW = new PerkName("HOOK_ARROW");

    public HookArrowPerk(CommonGame game) {
        super(ActivationType.PASSIVE, HOOK_ARROW, new Cooldown(Cooldown.Unit.ACTIVATIONS, 3), false, 8, Items.PERK_HOOK_ARROW, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_HOOK_ARROW_COOLDOWN, wb));
    }
}
