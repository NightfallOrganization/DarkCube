package eu.darkcube.system.pserver.plugin;

import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.v3.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.pserver.plugin.user.User;
import eu.darkcube.system.pserver.plugin.user.UserManager;
import net.md_5.bungee.api.chat.TextComponent;

public enum Message {

	INVALID_GAMEMODE("INVALID_GAMEMODE"), NOT_A_PLAYER("NOT_A_PLAYER"), PLAYER_NOT_FOUND("PLAYER_NOT_FOUND"),
	SHUTTING_DOWN_SERVER("SHUTTING_DOWN_SERVER"), COMMANDS_PREFIX("COMMANDS_PREFIX"),
	COMMANDS_COMMANDINFO("COMMANDS_COMMANDINFO"), INVALID_SENDER("INVALID_SENDER"), NO_PLAYER_FOUND("NO_PLAYER_FOUND"),
	TELEPORTED_TO_LOCATION_SINGLE("TELEPORTED_TO_LOCATION_SINGLE"),
	TELEPORTED_TO_LOCATION_MULTIPLE("TELEPORTED_TO_LOCATION_MULTIPLE"),
	TELEPORTED_TO_ENTITY_SINGLE("TELEPORTED_TO_ENTITY_SINGLE"),
	TELEPORTED_TO_ENTITY_MULTIPLE("TELEPORTED_TO_ENTITY_MULTIPLE"), CHANGED_GAMEMODE_SINGLE("CHANGED_GAMEMODE_SINGLE"),
	CHANGED_GAMEMODE_MULTIPLE("CHANGED_GAMEMODE_MULTIPLE"), CLEARED_EFFECT_SINGLE("CLEARED_EFFECT_SINGLE"),
	CLEARED_EFFECT_MULTIPLE("CLEARED_EFFECT_MULTIPLE"),
	CLEARED_MULTIPLE_EFFECTS_SINGLE("CLEARED_MULTIPLE_EFFECTS_SINGLE"),
	CLEARED_MULTIPLE_EFFECTS_MULTIPLE("CLEARED_MULTIPLE_EFFECTS_MULTIPLE"), ADDED_EFFECT_SINGLE("ADDED_EFFECT_SINGLE"),
	ADDED_EFFECT_MULTIPLE("ADDED_EFFECT_MULTIPLE"), COULD_NOT_ADD_EFFECT("COULD_NOT_ADD_EFFECT"),
	FLIGHT_CHANGED_SINGLE("FLIGHT_CHANGED_SINGLE"), FLIGHT_CHANGED_MULTIPLE("FLIGHT_CHANGED_MULTIPLE"),
	INVALID_ENTITY("INVALID_ENTITY"), CHANGED_DIFFICULTY_FOR_WORLD("CHANGED_DIFFICULTY_FOR_WORLD"),
	PSERVER_INVENTORY_TITLE("PSERVER_INVENTORY_TITLE"),
	USER_MANAGMENT_INVENTORY_TITLE("USER_MANAGMENT_INVENTORY_TITLE"),
	USER_MANAGMENT_USER_INVENTORY_TITLE("USER_MANAGMENT_USER_INVENTORY_TITLE"), KILLED_ENTITY("KILLED_ENTITY"),
	WOOLBATTLE_FORCEMAP("WOOLBATTLE_FORCEMAP"), WOOLBATTLE_REVIVED_PLAYER("WOOLBATTLE_REVIVED_PLAYER"),
	WOOLBATTLE_REVIVE_NO_TEAM_FOUND("WOOLBATTLE_REVIVE_NO_TEAM_FOUND"),
	WOOLBATTLE_SETLIFES_LIFES("WOOLBATTLE_SETLIFES_LIFES"),
	WOOLBATTLE_SETLIFES_TEAM_LIFES("WOOLBATTLE_SETLIFES_TEAM_LIFES"),
	WOOLBATTLE_SETTEAM_TEAM_SINGLE("WOOLBATTLE_SETTEAM_TEAM_SINGLE"),
	WOOLBATTLE_SETTEAM_TEAM_MULTIPLE("WOOLBATTLE_SETTEAM_TEAM_MULTIPLE"),
	WOOLBATTLE_TROLL_SETPERKCOOLDOWN_SINGLE("WOOLBATTLE_TROLL_SETPERKCOOLDOWN_SINGLE"),
	WOOLBATTLE_TROLL_SETPERKCOOLDOWN_MULTIPLE("WOOLBATTLE_TROLL_SETPERKCOOLDOWN_MULTIPLE"),
	WOOLBATTLE_TROLL_SETPERKCOST_SINGLE("WOOLBATTLE_TROLL_SETPERKCOST_SINGLE"),
	WOOLBATTLE_TROLL_SETPERKCOST_MULTIPLE("WOOLBATTLE_TROLL_SETPERKCOST_MULTIPLE"),
	WOOLBATTLE_TROLL_RESETPERKCOOLDOWN_SINGLE("WOOLBATTLE_TROLL_RESETPERKCOOLDOWN_SINGLE"),
	WOOLBATTLE_TROLL_RESETPERKCOOLDOWN_MULTIPLE("WOOLBATTLE_TROLL_RESETPERKCOOLDOWN_MULTIPLE"),
	WOOLBATTLE_TROLL_RESETPERKCOST_SINGLE("WOOLBATTLE_TROLL_RESETPERKCOST_SINGLE"),
	WOOLBATTLE_TROLL_RESETPERKCOST_MULTIPLE("WOOLBATTLE_TROLL_RESETPERKCOST_MULTIPLE"),
	WOOLBATTLE_TIMER("WOOLBATTLE_TIMER"),
	ITEM_LORE_USER_MANAGMENT_INVENTORY_USER("ITEM_LORE_USER_MANAGMENT_INVENTORY_USER"), ON("ON"), OFF("OFF"),
	NOT_COMMAND_BLOCK("NOT_COMMAND_BLOCK"), COMMAND_BLOCK_CONTENT("COMMAND_BLOCK_CONTENT"),
	CLEARED_COMMAND_BLOCK("CLEARED_COMMAND_BLOCK"),

	;

	public static final String PREFIX = "PServerPlugin_";
	public static final Function<String, String> KEY_MODIFIER = t -> PREFIX + t;

	private final String key;

	private Message(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public TextComponent[] getMessage(CommandSource source, Object... args) {
		return getMessage(source.getSource(), args);
	}

	public TextComponent[] getMessage(ICommandExecutor executor, Object... args) {
		return CustomComponentBuilder.cast(TextComponent.fromLegacyText(getMessageString(executor, args)));
	}

	public static String getMessageString(String messageKey, Language language, Object... args) {
		return getMessageString(messageKey, new String[0], language, args);
	}

	public static String getMessageString(String messageKey, String[] prefixes, Language language, Object... args) {
		return language.getMessage(PREFIX + String.join("", prefixes) + messageKey, args);
	}

	public String getMessageString(User user, Object... args) {
		return this.getMessageString(user.getCommandExecutor(), args);
	}

	public String getMessageString(ICommandExecutor executor, Object... args) {
		Language language = Language.DEFAULT;
		if (executor instanceof BukkitCommandExecutor) {
			CommandSender sender = ((BukkitCommandExecutor) executor).getSender();
			if (sender instanceof Player) {
				language = UserManager.getInstance().getUser((Player) sender).getLanguage();
			}
		}
		return getMessageString(key, language, args);
	}
}