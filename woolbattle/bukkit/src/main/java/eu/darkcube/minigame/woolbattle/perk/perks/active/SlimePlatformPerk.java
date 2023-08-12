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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class SlimePlatformPerk extends Perk {
    public static final PerkName SLIME_PLATFORM = new PerkName("SLIME_PLATFORM");

    public SlimePlatformPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, SLIME_PLATFORM, 20, 18, Item.PERK_SLIME_PLATFORM, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_SLIME_PLATFORM_COOLDOWN, woolbattle));
        addListener(new ListenerSlimePlatform(this, woolbattle));
    }

    public static class ListenerSlimePlatform extends BasicPerkListener {
        private final WoolBattleBukkit woolbattle;

        public ListenerSlimePlatform(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
            this.woolbattle = woolbattle;
        }

        @Override protected boolean activateRight(UserPerk perk) {

            Player p = perk.owner().getBukkitEntity();

            ArrayList<Block> blocks = new ArrayList<>();
            this.setBlock(p.getLocation().subtract(0, 5, 0), blocks);
            this.setBlock(p.getLocation().subtract(1, 5, 0), blocks);
            this.setBlock(p.getLocation().subtract(-1, 5, 0), blocks);
            this.setBlock(p.getLocation().subtract(0, 5, 1), blocks);
            this.setBlock(p.getLocation().subtract(0, 5, -1), blocks);

            return true;
        }

        @SuppressWarnings("deprecation") private void setBlock(Location block, ArrayList<Block> l) {
            if (block.getBlock().getType() == Material.AIR) {
                woolbattle.ingame().place(block.getBlock(), b -> {
                    BlockState state = b.getState();
                    state.setType(Material.STAINED_CLAY);
                    state.setData(Material.STAINED_CLAY.getNewData((byte) 13));
                    state.update(true);
                    l.add(b);
                    woolbattle.ingame().setMetaData(b, "slime", l);
                });
            }
        }

        private void setBlock2(Location block) {
            if (woolbattle.ingame().getMetaData(block.getBlock(), "slime", null) != null) {
                woolbattle.ingame().destroy(block.getBlock());
            }
        }

        @EventHandler public void moveEvent(PlayerMoveEvent e) {

            WBUser user = WBUser.getUser(e.getPlayer());
            if (!user.getTeam().canPlay()) return;

            ArrayList<Block> mv = woolbattle.ingame().getMetaData(e.getTo().clone().subtract(0, 1, 0).getBlock(), "slime", null);
            ArrayList<Block> mv2 = woolbattle.ingame().getMetaData(e.getTo().clone().subtract(0, 2, 0).getBlock(), "slime", null);

            if (mv != null || mv2 != null) {

                ArrayList<Block> blocks = mv == null ? mv2 : mv;
                for (Block b : blocks) {
                    setBlock2(b.getLocation());
                }

                e.getPlayer().setVelocity(new Vector(0, 3, 0).add(e.getPlayer().getVelocity().multiply(new Vector(0.1, -0.4, 0.1))));
            }

        }
    }

}
