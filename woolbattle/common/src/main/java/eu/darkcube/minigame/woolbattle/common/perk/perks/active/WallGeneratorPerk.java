package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class WallGeneratorPerk extends Perk {
    public static final PerkName WALL_GENERATOR = new PerkName("WALL_GENERATOR");

    public WallGeneratorPerk(CommonGame game) {
        super(ActivationType.ACTIVE, WALL_GENERATOR, 9, 1, Items.PERK_WALL_GENERATOR, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_WALL_GENERATOR_COOLDOWN, g));
    }
}
