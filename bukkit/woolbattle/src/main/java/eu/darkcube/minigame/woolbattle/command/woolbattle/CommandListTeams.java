package eu.darkcube.minigame.woolbattle.command.woolbattle;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.Command;

public class CommandListTeams extends Command {

	public CommandListTeams() {
		super(WoolBattle.getInstance(), "listTeams", new Command[0], "Listet alle Teams auf");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		TeamType[] types = TeamType.values();
		if (types.length == 0) {
			sender.sendMessage("§cEs sind keine Teams erstellt!");
			return true;
		}
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < types.length; i++) {
			TeamType t = types[i];
			b.append("§7- Name: '§5").append(t.getDisplayNameKey()).append("§7', Sortierung: §5").append(t.getWeight())
					.append("§7, Max. Spieler: §5").append(t.getMaxPlayers()).append("§7, Wollfarbe: §5")
					.append(DyeColor.getByData(t.getWoolColorByte())).append("§7, Namenfarbe: §5")
					.append(ChatColor.getByChar(t.getNameColor()).name()).append('\n');
		}
		sender.sendMessage(b.toString());
		return true;
	}
}