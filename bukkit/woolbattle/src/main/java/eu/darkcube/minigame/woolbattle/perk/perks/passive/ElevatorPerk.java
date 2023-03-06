/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive.ListenerElevator;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;

public class ElevatorPerk extends Perk {
	public static final PerkName ELEVATOR = new PerkName("ELEVATOR");

	public ElevatorPerk() {
		super(ActivationType.PASSIVE, ELEVATOR, new Cooldown(TimeUnit.TICKS, 3), false, 0,
				CostType.PER_ACTIVATION, Item.PERK_ELEVATOR,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_ELEVATOR_COOLDOWN));
		addListener(new ListenerElevator());
	}
}
