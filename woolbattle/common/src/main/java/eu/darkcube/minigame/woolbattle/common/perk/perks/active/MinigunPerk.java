package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class MinigunPerk extends Perk {
    public static final PerkName MINIGUN = new PerkName("MINIGUN");

    public MinigunPerk(CommonGame game) {
        super(ActivationType.ACTIVE, MINIGUN, 10, 1, Items.PERK_MINIGUN, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_MINIGUN_COOLDOWN, g));
    }
}
