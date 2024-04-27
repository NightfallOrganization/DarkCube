/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattle;

import net.minestom.server.MinecraftServer;

import java.util.Arrays;

public class WoolBattleMinestom {

    public void start(String[] args) {
        System.out.println(Arrays.toString(args));
        MinecraftServer server = MinecraftServer.init();
        server.start(System.getProperty("service.bind.host"), Integer.getInteger("service.bind.port"));
    }
}
