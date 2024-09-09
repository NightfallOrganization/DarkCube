package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class WoolBombPerk extends Perk {
    public static final PerkName WOOL_BOMB = new PerkName("WOOL_BOMB");

    public WoolBombPerk(CommonGame game) {
        super(ActivationType.ACTIVE, WOOL_BOMB, 14, 8, Items.PERK_WOOL_BOMB, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_WOOL_BOMB_COOLDOWN, g));
    }
}
