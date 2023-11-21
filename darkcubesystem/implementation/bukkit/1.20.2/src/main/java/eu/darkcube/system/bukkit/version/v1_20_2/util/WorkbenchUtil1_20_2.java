/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.version.v1_20_2.util;

import eu.darkcube.system.bukkit.util.WorkbenchUtil;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class WorkbenchUtil1_20_2 implements WorkbenchUtil {

    @Override public InventoryView openWorkbench(Player player, Location location, boolean force, Component title) {
        CraftPlayer cp = (CraftPlayer) player;

        BlockPos pos = CraftLocation.toBlockPosition(location);
        Level level = cp.getHandle().level();
        MenuProvider provider = new SimpleMenuProvider((syncId, inventory, p1) -> new CraftingMenu(syncId, inventory, ContainerLevelAccess.create(level, pos)), net.minecraft.network.chat.Component.literal(LegacyComponentSerializer
                .legacySection()
                .serialize(title)));

        cp.getHandle().openMenu(provider);
        if (force) cp.getHandle().containerMenu.checkReachable = false;
        return cp.getHandle().containerMenu.getBukkitView();
    }
}
