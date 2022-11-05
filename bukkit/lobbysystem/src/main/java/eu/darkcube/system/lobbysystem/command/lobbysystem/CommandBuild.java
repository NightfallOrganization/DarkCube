package eu.darkcube.system.lobbysystem.command.lobbysystem;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.EventGadgetSelect;
import eu.darkcube.system.lobbysystem.listener.ListenerSettingsJoin;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.UUIDManager;

public class CommandBuild extends Command {

	public CommandBuild() {
		super(Lobby.getInstance(), "build", new Command[] {}, "Wechsle den Bau-Modus",
				new Argument("Spieler", "Spieler zum Ausführen", false));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(p -> p.startsWith(args[0]))
					.collect(Collectors.toList());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player || args.length == 1) {
			Player t = null;
			if (args.length == 1) {
				t = UUIDManager.getPlayer(args[0]);
				if (t == null) {
					Lobby.getInstance().sendMessage("§cDieser Spieler konnte nicht gefunden werden!", sender);
					return true;
				}
			} else {
				t = (Player) sender;
			}
			String name = t.getName();
			User user = UserWrapper.getUser(t.getUniqueId());
			if (user.isBuildMode()) {
				PlayerJoinEvent e = new PlayerJoinEvent(t, "Custom PlayerJoinEvent");
				ListenerSettingsJoin.instance.handle(e);
				EventGadgetSelect e1 = new EventGadgetSelect(user, user.getGadget());
				Bukkit.getPluginManager().callEvent(e1);
				user.setBuildMode(false);
				Lobby.getInstance().sendMessage("§cNun nicht mehr im §eBau-Modus§c!", t);
				if (!sender.equals(t)) {
					Lobby.getInstance().sendMessage("§6" + name + "§c ist nun nicht mehr im §eBau-Modus§c!", sender);
				}
			} else {
				user.setBuildMode(true);
				t.setGameMode(GameMode.CREATIVE);
				Lobby.getInstance().sendMessage("§aNun im §eBau-Modus§a!", t);
				if (!sender.equals(t)) {
					Lobby.getInstance().sendMessage("§6" + name + "§a ist nun im §eBau-Modus§a!", sender);
				}
			}
			return true;
		}
		return false;
	}

}
