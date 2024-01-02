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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FreezeArrowPerk extends Perk {

    public static final PerkName FREEZE_ARROW = new PerkName("FREEZE_ARROW");

    public FreezeArrowPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.PASSIVE, FREEZE_ARROW, new Cooldown(Unit.ACTIVATIONS, 3), false, 4, CostType.PER_ACTIVATION, Item.PERK_FREEZE_ARROW, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_FREEZE_ARROW_COOLDOWN, wb));
        addListener(new FreezeArrowListener(woolbattle));
    }

    public class FreezeArrowListener implements Listener {
        private final WoolBattleBukkit woolbattle;

        public FreezeArrowListener(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
        }

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) public void handle(BowArrowHitPlayerEvent event) {
            if (event.arrow().hasMetadata("freezeArrow")) {
                UserPerk perk = (UserPerk) event.arrow().getMetadata("freezeArrow").get(0).value();
                int removed = event.shooter().removeWool(perk.perk().cost());
                if (removed < perk.perk().cost()) {
                    event.shooter().addWool(removed);
                    return;
                }
                var be = event.target().getBukkitEntity();
                if (be != null) be.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20 + 10, 10, true, false));
            }
        }

        @EventHandler public void handle(BowShootArrowEvent event) {
            for (UserPerk perk : event.user().perks().perks(perkName())) {
                if (perk.cooldown() == 0) {
                    perk.cooldown(perk.perk().cooldown().cooldown());
                    event.arrow().setMetadata("freezeArrow", new FixedMetadataValue(woolbattle, perk));
                    break;
                }
                perk.cooldown(perk.cooldown() - 1);
            }
        }
    }
}
