/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowArrowHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowShootArrowEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.FixedMetadataValue;

public class DrawArrowPerk extends Perk {
    public static final PerkName HOOK_ARROW = new PerkName("DRAW_ARROW");

    public DrawArrowPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.PASSIVE, HOOK_ARROW, new Cooldown(Unit.ACTIVATIONS, 3), false, 8, Item.PERK_DRAW_ARROW, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_DRAW_ARROW_COOLDOWN, woolbattle));
        addListener(new DrawArrowListener(woolbattle));
    }

    public class DrawArrowListener implements Listener {
        private final WoolBattleBukkit woolbattle;

        public DrawArrowListener(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
        }

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) public void handle(BowArrowHitPlayerEvent event) {
            if (event.arrow().hasMetadata("drawArrow")) {
                int removed = event.shooter().removeWool(cost());
                if (removed < cost()) {
                    event.shooter().addWool(removed);
                    return;
                }
                event
                        .shooter()
                        .getBukkitEntity()
                        .teleport(event
                                .target()
                                .getBukkitEntity()
                                .getLocation()
                                .setDirection(event.shooter().getBukkitEntity().getLocation().getDirection()), TeleportCause.PLUGIN);
            }
        }

        @EventHandler public void handle(BowShootArrowEvent event) {
            for (UserPerk perk : event.user().perks().perks(perkName())) {
                if (perk.cooldown() == 0) {
                    perk.cooldown(cooldown().cooldown());
                    event.arrow().setMetadata("drawArrow", new FixedMetadataValue(woolbattle, perk));
                    break;
                }
                perk.cooldown(perk.cooldown() - 1);
            }
        }
    }
}
