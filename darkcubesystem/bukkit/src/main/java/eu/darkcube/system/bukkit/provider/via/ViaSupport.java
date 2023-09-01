/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.provider.via;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface ViaSupport {

    static ViaSupport wrapper(@Nullable ViaSupport via) {
        return new ViaSupportWrapper(via);
    }

    boolean supported();

    int version(UUID uuid);

    int serverVersion();

    int[] supportedVersions();

    List<String> tabComplete(int playerVersion, Player player, String commandLine, ParseResults<CommandSource> parse, Suggestions suggestions);

}
