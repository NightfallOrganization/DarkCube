/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.minestom.WoolBattleMinestom;
import eu.darkcube.system.minestom.util.StoneWorld;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.world.biomes.Biome;

public class WoolBattleMinestomExtension extends Extension {

    private WoolBattleMinestom woolbattle;

    @Override
    public void initialize() {
        woolbattle = new WoolBattleMinestom();
        woolbattle.start();
    }

    @Override
    public void terminate() {
        woolbattle.stop();
    }
}
