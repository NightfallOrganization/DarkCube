/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockCanBuildEvent;

public class ListenerBlockCanBuild extends Listener<BlockCanBuildEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerBlockCanBuild(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    @EventHandler
    public void handle(BlockCanBuildEvent e) {
        if (e.getBlock().getType() != Material.AIR) {
            e.setBuildable(false);
            return;
        }
        if (e.getMaterial() == Material.WOOL) {
            e.setBuildable(true);
            var block = CraftMagicNumbers.getBlock(e.getMaterial());
            var box = block.a(((CraftWorld) e.getBlock().getWorld()).getHandle(), new BlockPosition(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), block.getBlockData());
            for (var ent : e.getBlock().getWorld().getEntities()) {
                var entityBox = ((CraftEntity) ent).getHandle().getBoundingBox();
                if (box.b(entityBox)) {
                    if (preventsBlockPlacement(ent)) {
                        e.setBuildable(false);
                        return;
                    }
                }
            }
        }
    }

    private boolean preventsBlockPlacement(Entity entity) {
        if (entity instanceof Player) {
            var p = (Player) entity;
            return !woolbattle.teamManager().getSpectator().contains(p.getUniqueId());
        }
        return false;
    }
}
