/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.util;

import eu.darkcube.system.lobbysystem.event.EventPServerMayJoin;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PServerUtil {
    public static CompletableFuture<Boolean> mayJoin(LobbyUser user, PServerExecutor pserver) {
        return pserver.accessLevel().thenApplyAsync(accessLevel -> {
            if (accessLevel == AccessLevel.PUBLIC) {
                return mayJoin(user, pserver, true);
            }
            try {
                if (pserver.owners().get().contains(user.user().getUniqueId())) {
                    return mayJoin(user, pserver, true);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return mayJoin(user, pserver, false);
        });
    }

    private static boolean mayJoin(LobbyUser user, PServerExecutor pserver, boolean mayJoin) {
        EventPServerMayJoin e = new EventPServerMayJoin(user, pserver, mayJoin);
        Bukkit.getPluginManager().callEvent(e);
        return e.mayJoin();
    }
}
