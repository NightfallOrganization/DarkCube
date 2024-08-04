package eu.darkcube.minigame.woolbattle.common.perk.perks.active;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class CapsulePerk extends Perk {
    public static final PerkName CAPSULE = new PerkName("CAPSULE");

    public CapsulePerk(CommonGame game) {
        super(ActivationType.ACTIVE, CAPSULE, 30, 24, Items.PERK_CAPSULE, (user, perk, id, perkSlot, g) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_CAPSULE_COOLDOWN, g));
    }
}
