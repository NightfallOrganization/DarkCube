/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.Line;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LineBuilderPerk extends Perk {
    public static final PerkName LINE_BUILDER = new PerkName("LINE_BUILDER");

    public LineBuilderPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, LINE_BUILDER, new Cooldown(Unit.TICKS, 10 * 20), true, 2, Item.PERK_LINE_BUILDER, (user, perk, id, perkSlot, wb) -> new LineBuilderUserPerk(user, id, perkSlot, perk, woolbattle));
        addListener(new ListenerLineBuilder(this, woolbattle));
    }

    public static class ListenerLineBuilder extends BasicPerkListener {

        private final Key DATA_SCHEDULER = Key.key(woolbattle, "linebuilder_scheduler");

        public ListenerLineBuilder(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
        }

        public static Location getNiceLocation(Location loc) {
            Location l = loc.clone();
            l.setYaw(getNiceYaw(l.getYaw()));
            l.setPitch(0);
            l.setX(l.getBlockX());
            l.setY(l.getBlockY());
            l.setZ(l.getBlockZ());
            return l;
        }

        public static float getNiceYaw(float y) {
            float interval = 45f;
            float half = interval / 2f;
            y %= 360F;

            if (y < 0) y = 360F + y;

            for (float i = 360; i >= 0; i -= interval) {
                float bound1 = i - half;
                float bound2 = i + half;
                if (y >= bound1 && y <= bound2) {
                    y = i;
                    break;
                }
            }
            return y;
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            WBUser user = perk.owner();
            if (!user.user().metadata().has(DATA_SCHEDULER)) {
                user.user().metadata().set(DATA_SCHEDULER, new TheScheduler(perk));
            }
            TheScheduler s = user.user().metadata().get(DATA_SCHEDULER);
            s.lastLine = s.tick;
            return false;
        }

        @Override
        protected boolean activateLeft(UserPerk perk) {
            perk.owner().user().metadata().remove(DATA_SCHEDULER);
            return false;
        }

        private class TheScheduler extends Scheduler {
            private int lastLine = 0;
            private Line line = null;
            private int cooldownTicks = 0;
            private UserPerk perk;
            private WBUser user;
            private int tick = 0;

            public TheScheduler(UserPerk perk) {
                super(woolbattle);
                this.perk = perk;
                this.user = perk.owner();
                runTaskTimer(1);
            }

            @Override
            public void run() {
                tick++;
                if (cooldownTicks > 1) {
                    cooldownTicks--;
                } else {
                    if (tick - lastLine > 5) {
                        user.user().metadata().remove(DATA_SCHEDULER);
                        cancel();
                        return;
                    }
                }
                if (tick - lastLine > 5) {
                    line = null;
                    return;
                }
                if (cooldownTicks > perk.perk().cooldown().cooldown()) {
                    perk.cooldown(perk.perk().cooldown().cooldown());
                    user.user().metadata().remove(DATA_SCHEDULER);
                    cancel();
                    return;
                }
                if (tick % 3 == 1) {
                    payForThePerk(perk);
                    cooldownTicks += (int) TimeUnit.SECOND.toUnit(TimeUnit.TICKS);
                    if (line == null) {
                        line = new Line(getNiceLocation(user.getBukkitEntity().getLocation()).getDirection());
                    }
                    Location next = line.getNextBlock(user.getBukkitEntity().getLocation());
                    line.addBlock(next);
                    woolbattle.ingame().place(user, next.getBlock());
                    user.getBukkitEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 10, false, false), true);
                }
            }
        }
    }

    public static class LineBuilderUserPerk extends CooldownUserPerk {

        private boolean useCooldownItem = false;

        public LineBuilderUserPerk(WBUser owner, int id, int perkSlot, Perk perk, WoolBattleBukkit woolbattle) {
            super(owner, id, perkSlot, perk, Item.PERK_LINE_BUILDER_COOLDOWN, woolbattle);
        }

        @Override
        public boolean useCooldownItem() {
            return useCooldownItem;
        }

        @Override
        public void cooldown(int cooldown) {
            super.cooldown(cooldown);
            if (cooldown() >= perk().cooldown().cooldown()) {
                useCooldownItem = true;
            } else if (cooldown == 0) {
                useCooldownItem = false;
            }
        }
    }
}
