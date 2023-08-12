/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.other;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.active.EventGhostStateChange;
import eu.darkcube.minigame.woolbattle.event.perk.other.DoubleJumpEvent;
import eu.darkcube.minigame.woolbattle.event.perk.passive.EventMayDoubleJump;
import eu.darkcube.minigame.woolbattle.event.user.EventUserWoolCountUpdate;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.PerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.SpiderPerk.SpiderStateChangeEvent;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class DoubleJumpPerk extends Perk {
    public static final PerkName DOUBLE_JUMP = new PerkName("DOUBLE_JUMP");

    public DoubleJumpPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.DOUBLE_JUMP, DOUBLE_JUMP, new Cooldown(Unit.TICKS, 63), 5, null, (owner, perk, id, perkSlot, wb) -> new DoubleJumpUserPerk(owner, perk, id, perkSlot, woolbattle));
        addListener(new ListenerDoubleJump(this, woolbattle));
    }

    public static class ListenerDoubleJump extends PerkListener {
        private final WoolBattleBukkit woolbattle;

        public ListenerDoubleJump(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk);
            this.woolbattle = woolbattle;
        }

        @EventHandler public void handle(PlayerToggleFlightEvent e) {
            Player p = e.getPlayer();
            WBUser user = WBUser.getUser(p);
            if (p.getGameMode() != GameMode.SURVIVAL || user.isTrollMode() || !user.getTeam().canPlay()) {
                return;
            }

            RefreshResult res = refresh(user);
            if (res.state == RefreshResult.ALLOWED && e.isFlying()) {
                e.setCancelled(true);
                res.perk.cooldown(res.perk.perk().cooldown().cooldown());
                Vector velo = p.getLocation().getDirection().setY(0).normalize().multiply(0.11).setY(1.05);
                p.setAllowFlight(false);

                DoubleJumpEvent event = new DoubleJumpEvent(user, velo);
                Bukkit.getPluginManager().callEvent(event);

                p.setVelocity(event.velocity());
                payForThePerk(user, perk());
            } else if (res.state == RefreshResult.DENIED && e.isFlying()) {
                e.setCancelled(true);
                p.setAllowFlight(false);
            } else if (res.state == RefreshResult.INSUFFICIENT_WOOL && e.isFlying()) {
                e.setCancelled(true);
                p.setAllowFlight(false);
            } else if (res.state == RefreshResult.EVENT_CANCELLED) {
                // Do nothing. This is for things like spider
            }
        }

        @EventHandler public void handle(EventUserWoolCountUpdate event) {
            workRefresh(event.user());
        }

        @EventHandler public void handle(EventGhostStateChange event) {
            workRefresh(event.user());
        }

        @EventHandler public void handle(PlayerGameModeChangeEvent e) {
            new Scheduler(woolbattle, () -> workRefresh(WBUser.getUser(e.getPlayer()))).runTask();
        }

        @EventHandler public void handle(SpiderStateChangeEvent event) {
            new Scheduler(woolbattle, () -> workRefresh(event.user())).runTask();
        }

        private void workRefresh(WBUser user) {
            RefreshResult res = refresh(user);
            switch (res.state) {
                case RefreshResult.ALLOWED:
                    user.getBukkitEntity().setAllowFlight(true);
                    break;
                case RefreshResult.DENIED:
                case RefreshResult.INSUFFICIENT_WOOL:
                    user.getBukkitEntity().setAllowFlight(false);
            }
        }

        private RefreshResult refresh(WBUser user) {
            Player p = user.getBukkitEntity();
            UserPerk ownerPerk = null;
            UserPerk fallbackPerk = null;
            if (p.getGameMode() == GameMode.SURVIVAL) {
                for (UserPerk perk : user.perks().perks(DoubleJumpPerk.DOUBLE_JUMP)) {
                    if (fallbackPerk == null) fallbackPerk = perk;
                    if (user.woolCount() >= perk.perk().cost() && perk.cooldown() == 0) {
                        ownerPerk = perk;
                        break;
                    }
                }
            }
            if (ownerPerk != null) {
                EventMayDoubleJump event = new EventMayDoubleJump(ownerPerk, true);
                Bukkit.getPluginManager().callEvent(event);
                boolean mayDoubleJump = event.mayDoubleJump();
                boolean cancel = event.isCancelled();
                if (!cancel) {
                    if (mayDoubleJump) {
                        return new RefreshResult(RefreshResult.ALLOWED, ownerPerk);
                    }
                    return new RefreshResult(RefreshResult.DENIED, ownerPerk);
                }
                return new RefreshResult(RefreshResult.EVENT_CANCELLED, ownerPerk);
            }
            return new RefreshResult(RefreshResult.INSUFFICIENT_WOOL, fallbackPerk);
        }

        private static class RefreshResult {
            private static final int INSUFFICIENT_WOOL = 0, EVENT_CANCELLED = 1, ALLOWED = 2, DENIED = 3;
            private final int state;
            private final UserPerk perk;

            public RefreshResult(int state, UserPerk perk) {
                this.state = state;
                this.perk = perk;
            }
        }
    }

    public static class DoubleJumpUserPerk extends DefaultUserPerk {

        public DoubleJumpUserPerk(WBUser owner, Perk perk, int id, int perkSlot, WoolBattleBukkit woolbattle) {
            super(owner, perk, id, perkSlot, woolbattle);
        }

        @Override public void cooldown(int cooldown) {
            float max = perk().cooldown().cooldown();
            owner().getBukkitEntity().setFoodLevel(7 + (Math.round((max - (float) cooldown) / max * 13)));
            if (cooldown <= 0 && cooldown() > 0) {
                if (!owner().getBukkitEntity().getAllowFlight()) owner().getBukkitEntity().setAllowFlight(true);
            }
            super.cooldown(cooldown);
        }
    }
}
