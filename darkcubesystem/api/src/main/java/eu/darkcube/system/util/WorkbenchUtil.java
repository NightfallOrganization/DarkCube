/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public interface WorkbenchUtil {

    InventoryView openWorkbench(Player player, Component title);

}
