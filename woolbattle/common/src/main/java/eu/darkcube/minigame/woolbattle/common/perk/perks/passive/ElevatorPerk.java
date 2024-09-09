package eu.darkcube.minigame.woolbattle.common.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;

public class ElevatorPerk extends Perk {
    public static final PerkName ELEVATOR = new PerkName("ELEVATOR");

    public ElevatorPerk(CommonGame game) {
        super(ActivationType.PASSIVE, ELEVATOR, new Cooldown(Cooldown.Unit.ACTIVATIONS, 2), false, 0, Items.PERK_ELEVATOR, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Items.PERK_ELEVATOR_COOLDOWN, wb));
    }
}
