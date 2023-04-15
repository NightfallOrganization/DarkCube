/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.util.SkullCache;

public class CommandShowSkullCache extends LobbyCommandExecutor {

	public CommandShowSkullCache() {
		super("showSkullCache", b -> {
			b.executes(ctx -> {
				StringBuilder sb = new StringBuilder();
				sb.append("SkullCache (").append(SkullCache.cache.size()).append("):\n");
				SkullCache.cache.forEach((key, value) -> sb.append(" - ")
						.append(SkullCache.getCachedItem(key).getItemMeta().getDisplayName()));
				ctx.getSource().sendMessage(
						LegacyComponentSerializer.legacySection().deserialize(sb.toString()));
				return 0;
			});
		});
	}

}
