/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.permissions.PermissionAttachmentInfo;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerPlayerLogin extends Listener<PlayerLoginEvent> {
	@Override
	@EventHandler(priority = EventPriority.HIGHEST)
	public void handle(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		PermissionInfo info = getPermissionInfo(p);

		boolean full = WoolBattle.getInstance().getMaxPlayers() <= WoolBattle.getInstance().getUserWrapper().getUsers().size();
		boolean shouldKick = info.hasPermission && full;

		Player weakestLink = null;
		PermissionInfo weakestLinkInfo = null;
		shouldKick: if (shouldKick) {
			for (Player o : Bukkit.getOnlinePlayers()) {
				if (o != p) {
					PermissionInfo oInfo = getPermissionInfo(o);
					if (weakestLink == null) {
						if (oInfo.compareTo(info) <= 0) {
							weakestLink = o;
							weakestLinkInfo = oInfo;
						}
						continue;
					}
					if (oInfo.compareTo(info) < 0) {
						if (weakestLinkInfo.compareTo(oInfo) > 0) {
							weakestLink = o;
							weakestLinkInfo = oInfo;
						}
					}
				}
			}
			if (weakestLink == null) {
				break shouldKick;
			}
			weakestLink.kickPlayer("§cDu wurdest für einen anderen Spieler gekickt!");
			return;
		}
		if (full) {
			e.disallow(Result.KICK_BANNED, "§cDieser Server ist voll!");
		}
//		if (Main.getInstance().getMaxPlayers() <= Main.getInstance().getUserWrapper().getUsers().size()) {
//			e.disallow(Result.KICK_FULL, e.getKickMessage());
//		}
	}

	public static class PermissionInfo implements Comparable<PermissionInfo> {
		public boolean hasPermission = false;
		public int priority = 0;

		@Override
		public String toString() {
			return "PermissionInfo [hasPermission=" + hasPermission + ", priority=" + priority + "]";
		}

		@Override
		public int compareTo(PermissionInfo o) {
			return hasPermission ? Integer.valueOf(priority).compareTo(o.priority) : -1;
		}
	}

	public static PermissionInfo getPermissionInfo(Player p) {
		PermissionInfo pInfo = new PermissionInfo();
		for (PermissionAttachmentInfo info : p.getEffectivePermissions()) {
			if (info.getPermission().contains("fulljoin")) {
				pInfo.hasPermission = true;
				String[] split = info.getPermission().split("\\.");
				int prio = 0;
				try {
					prio = Integer.parseInt(split[split.length - 1]);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (pInfo.priority < prio) {
					pInfo.priority = prio;
				}
			}
		}
		return pInfo;
	}
}
