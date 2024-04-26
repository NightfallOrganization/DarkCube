/*
 * Copyright (c) 2023-2024. [DarkCube]
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
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class SwitcherPerk extends Perk {
    public static final PerkName SWITCHER = new PerkName("SWITCHER");

    public SwitcherPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, SWITCHER, 7, 8, Item.PERK_SWITCHER, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_SWITCHER_COOLDOWN, woolbattle));
        addListener(new ListenerSwitcher(this, woolbattle));
    }

    public static class ListenerSwitcher extends BasicPerkListener {

        public ListenerSwitcher(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            Snowball ball = perk.owner().getBukkitEntity().launchProjectile(Snowball.class);
            ball.setMetadata("perk", new FixedMetadataValue(woolbattle, perk.perk().perkName().getName()));
            return true;
        }

        @EventHandler
        public void handle(EntityDamageByEntityEvent e) {
            if (!(e.getEntity() instanceof Player)) {
                return;
            }
            if (e.getDamager().getType() != EntityType.SNOWBALL) {
                return;
            }
            Snowball snowball = (Snowball) e.getDamager();
            if (!(snowball.getShooter() instanceof Player)) {
                return;
            }
            Player p = (Player) snowball.getShooter();
            Player hit = (Player) e.getEntity();
            if (!snowball.getMetadata("perk").isEmpty() && snowball.getMetadata("perk").get(0).asString().equals(SwitcherPerk.SWITCHER.getName())) {
                e.setCancelled(true);
                WBUser user = WBUser.getUser(hit);
                if (user.projectileImmunityTicks() > 0) return;
                if (!woolbattle.ingame().playerUtil().canAttack(WBUser.getUser(p), user)) return;
                if (user.getTicksAfterLastHit() < TimeUnit.SECOND.toTicks(30)) woolbattle.ingame().playerUtil().attack(WBUser.getUser(p), user);
                Location loc = p.getLocation();
                p.teleport(hit);
                hit.teleport(loc);

                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1.8F);
                hit.playSound(hit.getLocation(), Sound.ITEM_PICKUP, 1, 1.8F);
            }
        }
    }

}
