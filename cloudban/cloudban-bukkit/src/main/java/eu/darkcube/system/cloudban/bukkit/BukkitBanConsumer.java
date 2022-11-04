package eu.darkcube.system.cloudban.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import eu.darkcube.system.cloudban.util.BanConsumer;
import eu.darkcube.system.cloudban.util.Punisher;
import eu.darkcube.system.cloudban.util.ban.Ban;

public class BukkitBanConsumer implements BanConsumer {

	private Player p;
	private PlayerLoginEvent e;

	public BukkitBanConsumer(Player p) {
		this.p = p;
	}

	public BukkitBanConsumer(PlayerLoginEvent e) {
		this.e = e;
	}

	@Override
	public void accept(Ban ban) {
		if (p != null) {
			Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), new Runnable() {
				@Override
				public void run() {
					if (!p.hasPermission(Punisher.PERMISSION_IGNORE_BAN)) {
						p.kickPlayer(getMessage(ban));
					}
				}
			});
		} else if (e != null) {
			e.disallow(PlayerLoginEvent.Result.KICK_BANNED, getMessage(ban));
		}
	}

	private String getMessage(Ban ban) {
		return ChatColor.translateAlternateColorCodes('&',
				Ban.BAN_MESSAGE.replace("%reason%", ban.getReason().getDisplay())
						.replace("%bannedat%", ban.getTimeBanned().toDate())
						.replace("%timeremain%", ban.getDuration().endingIn(ban.getTimeBanned())));
	}
}
