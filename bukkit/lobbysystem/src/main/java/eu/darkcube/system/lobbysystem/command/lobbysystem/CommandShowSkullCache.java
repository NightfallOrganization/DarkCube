/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandShowSkullCache extends LobbyCommandExecutor {

	public CommandShowSkullCache() {
		super("showSkullCache", b -> {
			b.executes(ctx -> {
				StringBuilder sb = new StringBuilder();
				sb.append("SkullCache (").append(SkullCache.cache.size()).append("):\n");
				SkullCache.cache.entrySet().forEach(e -> {
					sb.append(" - ").append(
							SkullCache.getCachedItem(e.getKey()).getItemMeta().getDisplayName());
				});
				ctx.getSource().sendFeedback(
						CustomComponentBuilder.cast(TextComponent.fromLegacyText(sb.toString())),
						true);
				return 0;
			});
		});
	}

}
