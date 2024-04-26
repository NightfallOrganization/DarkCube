/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowArrowHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.event.perk.other.PlayerHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.event.user.EventUserAttackUser;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BerserkerPerk extends Perk {
    public static final PerkName BERSERKER = new PerkName("BERSERKER");

    private final Key combo;

    public BerserkerPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.PASSIVE, BERSERKER, new Cooldown(Unit.ACTIVATIONS, 0), 0, Item.PERK_BERSERKER, BerserkerUserPerk::new);
        combo = new Key(woolbattle, "perk_berserker_combo");
        addListener(new BerserkerListener(woolbattle));
    }

    private static class BerserkerUserPerk extends DefaultUserPerk {
        private final Key combo;

        public BerserkerUserPerk(WBUser owner, Perk perk, int id, int perkSlot, WoolBattleBukkit woolbattle) {
            super(owner, perk, id, perkSlot, woolbattle);
            combo = new Key(woolbattle, "perk_berserker_combo");
        }

        private int getHits() {
            return owner().user().metadata().getOr(combo, 0);
        }

        @Override public PerkItem currentPerkItem() {
            return new PerkItem(this::currentItem, this) {
                @Override protected void modify(ItemBuilder item) {
                    item.glow(!item.glow());
                }

                @Override protected int itemAmount() {
                    return getHits();
                }
            };
        }
    }

    public class BerserkerListener implements Listener {
        private final WoolBattleBukkit woolbattle;

        public BerserkerListener(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
        }

        @EventHandler public void handle(EventUserAttackUser event) {
            if (event.target().user().metadata().has(combo)) {
                event.target().user().metadata().remove(combo);
                for (UserPerk perk : event.target().perks().perks(perkName())) {
                    perk.currentPerkItem().setItem();
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) public void handle(BowArrowHitPlayerEvent event) {
            for (UserPerk perk : event.shooter().perks().perks(perkName())) {
                int combo = event.shooter().user().metadata().getOr(BerserkerPerk.this.combo, 0) + 1;
                event.shooter().user().metadata().set(BerserkerPerk.this.combo, combo);
                perk.currentPerkItem().setItem();
            }
        }

        @EventHandler public void handle(PlayerHitPlayerEvent event) {
            for (UserPerk perk : event.attacker().perks().perks(perkName())) {
                if (woolbattle.ingame().playerUtil().attack(event.attacker(), event.target())) {
                    boolean far = event
                            .target()
                            .getBukkitEntity()
                            .getLocation()
                            .distanceSquared(event.attacker().getBukkitEntity().getLocation()) > 200 * 200;
                    double x = far ? (Math.random() - Math.random()) : event.target().getBukkitEntity().getLocation().getX() - event
                            .attacker()
                            .getBukkitEntity()
                            .getLocation()
                            .getX();
                    double z = far ? Math.random() - Math.random() : event.target().getBukkitEntity().getLocation().getZ() - event
                            .attacker()
                            .getBukkitEntity()
                            .getLocation()
                            .getZ();
                    while (x * x + z * z < 1.0E-4D) {
                        x = (Math.random() - Math.random()) * 0.01D;
                        z = (Math.random() - Math.random()) * 0.01D;
                    }
                    Vector add = new Vector(x, 0, z).normalize();
                    int combo = event.attacker().user().metadata().getOr(BerserkerPerk.this.combo, 0) + 1;
                    if (combo > 9) combo = 9;
                    event.attacker().user().metadata().set(BerserkerPerk.this.combo, combo);
                    ItemStack hand = event.attacker().getBukkitEntity().getItemInHand();
                    double kb = hand.getEnchantments().getOrDefault(Enchantment.KNOCKBACK, 0) + .5;
                    double mul = Math.pow(combo, 0.4) * kb * 1.7;
                    add.multiply(mul);
                    add.multiply(.7);
                    if (add.getX() >= 4) {
                        add = add.multiply(3.98 / add.getX());
                    }
                    if (add.getZ() >= 4) {
                        add = add.multiply(3.98 / add.getZ());
                    }
                    add.setY(0.40001);

                    event.target().getBukkitEntity().damage(0);
                    event.target().getBukkitEntity().setVelocity(add);
                    event.setCancelled(true);
                    perk.currentPerkItem().setItem();
                }
            }
        }
    }
}
