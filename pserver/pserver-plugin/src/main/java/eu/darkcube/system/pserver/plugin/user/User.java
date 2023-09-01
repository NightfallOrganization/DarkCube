/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import com.google.gson.JsonObject;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.Language;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface User {

    Language getLanguage();

    CommandExecutor getCommandExecutor();

    UUID getUUID();

    @Nullable Player getOnlinePlayer();

    JsonObject getExtra();

    void setExtra(JsonObject extra);

    void saveExtra();

    boolean isOnline();

}
