/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.pserver;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.util.DataManager;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;

public class CommandMigrateAll extends LobbyCommand {
    public CommandMigrateAll() {
        super("migrateAll", b -> b.executes(ctx -> {
            var registered = PServerProvider.instance().registeredPServers().join();
            for (UniqueId id : registered) {
                var ex = PServerProvider.instance().pserver(id).join();
                DataManager.convertPServer(ex);
            }
            ctx.getSource().sendMessage(Component.text("Migration complete!"));
            return 0;
        }));
    }
}
