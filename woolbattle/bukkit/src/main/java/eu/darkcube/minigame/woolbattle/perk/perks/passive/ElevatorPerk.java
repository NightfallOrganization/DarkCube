/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import java.util.Collection;

import eu.darkcube.minigame.woolbattle.event.perk.passive.EventEnderPearl;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ElevatorPerk extends Perk {
    public static final PerkName ELEVATOR = new PerkName("ELEVATOR");

    public ElevatorPerk() {
        super(ActivationType.PASSIVE, ELEVATOR, new Cooldown(Unit.ACTIVATIONS, 2), false, 0, Item.PERK_ELEVATOR, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_ELEVATOR_COOLDOWN, wb));
        addListener(new ListenerElevator());
    }

    private static class ListenerElevator implements Listener {
        @EventHandler
        public void handle(EventEnderPearl event) {
            WBUser user = event.user();
            Collection<UserPerk> perks = user.perks().perks(ElevatorPerk.ELEVATOR);
            if (perks.isEmpty()) return;
            for (UserPerk perk : perks) {
                if (perk.cooldown() == 0) {
                    if (event.canElevate()) {
                        event.elevate(true);
                        perk.cooldown(perk.perk().cooldown().cooldown());
                    }
                } else {
                    perk.cooldown(perk.cooldown() - 1);
                }
            }
        }
    }
}
