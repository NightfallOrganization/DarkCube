/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.util.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class CommandLanguage extends Command {

	public CommandLanguage() {
		super(WoolBattle.getInstance(), "language", new Command[0], "Change your language",
				new Argument("language", "Your new language"));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			if (args.length == 1) {
				String lang = args[0];
				Language language = null;
				for (Language l : Language.values()) {
					if (lang.equalsIgnoreCase(l.getLocale().getDisplayName(l.getLocale()))) {
						language = l;
						break;
					}
				}
				if (language == null) {
					sender.sendMessage(Message.UNKNOWN_LANGUAGE.getMessage(user, lang));
					return true;
				}
				user.setLanguage(language);
				sender.sendMessage(Message.CHANGED_LANGUAGE.getMessage(user,
						language.getLocale().getDisplayName(user.getLanguage().getLocale())));
				if (WoolBattle.getInstance().getLobby().isEnabled())
					WoolBattle.getInstance().getLobby().listenerPlayerJoin.handle(new PlayerJoinEvent(p, null));
				if(WoolBattle.getInstance().getIngame().isEnabled())
					WoolBattle.getInstance().getIngame().setPlayerItems(user);
			} else {
				sender.sendMessage(Message.COMMAND_LANGUAGE_USAGE.getMessage(user));
			}
		} else {
			sender.sendMessage(Message.NO_PLAYER.getServerMessage());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(Language.values(), args[0].toLowerCase());
		}
		return super.onTabComplete(args);
	}
}
