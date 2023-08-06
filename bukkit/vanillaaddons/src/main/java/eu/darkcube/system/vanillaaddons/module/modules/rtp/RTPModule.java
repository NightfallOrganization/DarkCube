/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.rtp;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RTPModule extends CommandExecutor implements Module {

    public RTPModule() {
        super("vanillaaddons", "randomtp", new String[]{"rtp"}, b -> b.executes(ctx -> {
            Player player = ctx.getSource().asPlayer();
            World world = player.getWorld();
            ctx.getSource().sendMessage(Component.text("Teleporting...", NamedTextColor.GREEN));
            getRandomLocation(world).thenAccept(loc -> player.teleportAsync(loc, TeleportCause.COMMAND));
            return 0;
        }));
    }

    private static CompletableFuture<Location> getRandomLocation(World world) {
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
        int fx = (int) x;
        int fz = (int) z;
        return world.getChunkAtAsyncUrgently(new Location(world, x, 0, z)).thenApply(chunk -> {
            int y = chunk.getWorld().getHighestBlockYAt(fx, fz);
            return new Location(world, fx, y + 1, fz);
        });
    }

    @Override public void onEnable() {
        CommandAPI.instance().register(this);
    }

    @Override public void onDisable() {
        CommandAPI.instance().unregister(this);
    }
}
