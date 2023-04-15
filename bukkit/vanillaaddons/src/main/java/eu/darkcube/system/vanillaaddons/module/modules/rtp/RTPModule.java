/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.rtp;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Arrays;
import java.util.Random;

public class RTPModule extends CommandExecutor implements Module {
	private static final Material[] blockedMaterials =
			new Material[] {Material.LAVA, Material.WATER};

	public RTPModule() {
		super("vanillaaddons", "randomtp", new String[] {"rtp"}, b -> b.executes(ctx -> {
			Player player = ctx.getSource().asPlayer();
			World world = player.getWorld();
			Location loc;
			do {
				loc = getRandomLocation(world);
			} while (Arrays.asList(blockedMaterials).contains(loc.getBlock().getType()));
			player.teleportAsync(loc.add(0, 1, 0), TeleportCause.COMMAND);
			return 0;
		}));
	}

	private static Location getRandomLocation(World world) {
		int maxCoord = (int) (world.getWorldBorder().getSize() / 2) - 10;// 56
		Random random = new Random();// 57
		double x = world.getWorldBorder().getCenter().x() + random.nextInt(maxCoord);// 58
		double z = world.getWorldBorder().getCenter().y() + random.nextInt(maxCoord);// 59
		if (random.nextBoolean()) {
			x *= -1.0;// 61
		}

		if (random.nextBoolean()) {// 62
			z *= -1.0;
		}

		return new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z);// 64
	}

	@Override
	public void onEnable() {
		CommandAPI.getInstance().register(this);
	}

	@Override
	public void onDisable() {
		CommandAPI.getInstance().unregister(this);
	}
}
