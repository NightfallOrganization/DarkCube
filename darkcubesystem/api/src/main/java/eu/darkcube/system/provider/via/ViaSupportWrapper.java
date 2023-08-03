/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.provider.via;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

class ViaSupportWrapper implements ViaSupport {

    private final ViaSupport via;

    public ViaSupportWrapper(ViaSupport via) {
        this.via = via;
    }

    @Override
    public boolean supported() {
        return via != null && via.supported();
    }

    @Override
    public int version(UUID uuid) {
        return via.version(uuid);
    }

    @Override
    public int serverVersion() {
        return via.serverVersion();
    }

    @Override
    public List<String> tabComplete(int playerVersion, Player player, String commandLine, ParseResults<CommandSource> parse, List<Suggestion> completions) {
        return via.tabComplete(playerVersion,player,commandLine,parse,completions);
    }
}
