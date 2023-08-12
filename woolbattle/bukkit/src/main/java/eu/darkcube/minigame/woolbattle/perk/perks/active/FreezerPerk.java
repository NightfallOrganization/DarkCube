/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FreezerPerk extends Perk {
    public static final PerkName FREEZER = new PerkName("FREEZER");

    public FreezerPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, FREEZER, 6, 6, Item.PERK_FREEZER, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_FREEZER_COOLDOWN, woolbattle));
        addListener(new ListenerFreezer(this, woolbattle));
    }

    public static class ListenerFreezer extends BasicPerkListener {
        public ListenerFreezer(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
        }

        @Override protected boolean activateRight(UserPerk perk) {
            Snowball snowball = perk.owner().getBukkitEntity().launchProjectile(Snowball.class);
            snowball.setMetadata("perk", new FixedMetadataValue(woolbattle, perk.perk().perkName().toString()));
            return true;
        }

        @EventHandler public void handle(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }
            if (event.getDamager().getType() != EntityType.SNOWBALL) {
                return;
            }
            Snowball snowball = (Snowball) event.getDamager();
            if (!(snowball.getShooter() instanceof Player)) {
                return;
            }
            Player p = (Player) snowball.getShooter();
            Player hit = (Player) event.getEntity();
            if (!snowball.getMetadata("perk").isEmpty() && snowball
                    .getMetadata("perk")
                    .get(0)
                    .asString()
                    .equals(FreezerPerk.FREEZER.getName())) {
                event.setCancelled(true);
                WBUser user = WBUser.getUser(hit);
                if (user.projectileImmunityTicks() > 0) return;
                if (user.getTeam().getType() != WBUser.getUser(p).getTeam().getType()) {
                    user
                            .getBukkitEntity()
                            .addPotionEffect(new PotionEffect(PotionEffectType.SLOW, TimeUnit.SECOND.itoTicks(3), 6, true, false), true);
                }
            }
        }
    }

}
