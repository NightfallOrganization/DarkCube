/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version.v1_20_1.util;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.util.WorkbenchUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class WorkbenchUtil1_20_1 implements WorkbenchUtil {
    @Override public InventoryView openWorkbench(Player player, Component name) {
        CraftPlayer cp = (CraftPlayer) player;

        BlockPos pos = CraftLocation.toBlockPosition(cp.getLocation());
        Level level = cp.getHandle().level();
        MenuProvider provider = new SimpleMenuProvider((syncId, inventory, p1) -> new CraftingMenu(syncId, inventory, ContainerLevelAccess.create(level, pos)), net.minecraft.network.chat.Component.literal(LegacyComponentSerializer
                .legacySection()
                .serialize(name)));

        cp.getHandle().openMenu(provider);
        cp.getHandle().containerMenu.checkReachable = false;
        return cp.getHandle().containerMenu.getBukkitView();
    }
}
