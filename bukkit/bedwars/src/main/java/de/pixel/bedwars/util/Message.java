package de.pixel.bedwars.util;

import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum Message {

	INVENTORY_TITLE_LOBBY_VOTING_GOLD("INVENTORY_TITLE_LOBBY_VOTING_GOLD"),
	INVENTORY_TITLE_LOBBY_VOTING_IRON("INVENTORY_TITLE_LOBBY_VOTING_IRON"),
	VOTED_FOR_GOLD("VOTED_FOR_GOLD"), VOTED_AGAINST_GOLD("VOTED_AGAINST_GOLD"),
	VOTED_FOR_IRON("VOTED_FOR_IRON"), VOTED_AGAINST_IRON("VOTED_AGAINST_IRON"),
	VOTED_FOR_MAP("VOTED_FOR_MAP"),
	LOBBY_OBJECTIVE_TITLE("LOBBY_OBJECTIVE_TITLE"),
	LOBBY_PLAYER_JOINED("LOBBY_PLAYER_JOINED"),
	LOBBY_PLAYER_LEFT("LOBBY_PLAYER_LEFT"),
	INGAME_PLAYER_RECONNECTED("INGAME_PLAYER_RECONNECTED"),
	INGAME_PLAYER_LEFT("INGAME_PLAYER_LEFT"),
	JOINED_TEAM("JOINED_TEAM"),
	FULL_TEAM("FULL_TEAM"),
	CANT_BREAK_OWN_BED("CANT_BREAK_OWN_BED"),
	YOU_HAVE_BROKEN_BED("YOU_HAVE_BROKEN_BED"),
	BED_WAS_BROKEN("BED_WAS_BROKEN"),
	OWN_BED_BROKEN("OWN_BED_BROKEN"),
	PLAYER_DIED("PLAYER_DIED"),
	PLAYER_WAS_KILLED("PLAYER_WAS_KILLED"),
	PLAYER_WAS_FINAL_KILLED("PLAYER_WAS_FINAL_KILLED"),
	AT_ALL("AT_ALL"),
	TEAM_WON("TEAM_WON"),
	TEAM_ELIMINATED("TEAM_ELIMINATED"), 
	SHOP_INVENTORY_TITLE("SHOP_INVENTORY_TITLE"), 
	GOLD("GOLD"),
	IRON("IRON"),
	BRONZE("BRONZE"), 
	NONE("NONE"),
	INVENTORY_FULL("INVENTORY_FULL"), 
	ATTACK_DAMAGE("ATTACK_DAMAGE"),

	;

	public static final String ITEM_PREFIX = "ITEM_";
	public static final String LORE_PREFIX = "LORE_";
	public static final String TEAM_PREFIX = "TEAM_";

	private final String key;

	Message(String key) {
		this.key = key;
	}

	public static final String getMessage(String messageKey, Locale language, String... replacements) {
		try {
			String msg = I18n.translate(language, messageKey);
			for (int i = 0; msg.contains("{}") && i < replacements.length; i++) {
				msg = msg.replaceFirst("\\{\\}", replacements[i]);
			}
			return ChatColor.translateAlternateColorCodes('&', msg);
		} catch (Exception ex) {
			StringBuilder builder = new StringBuilder();
			builder.append(messageKey);
			if (replacements.length > 0) {
				builder.append('[');
				for (int i = 0; i + 1 < replacements.length; i++) {
					builder.append(replacements[i]).append(',');
				}
				builder.append(replacements[replacements.length - 1]).append(']');
			}
			return builder.toString();
		}
	}

	public final String getMessage(Locale language, String... replacements) {
		return getMessage(key, language, replacements);
	}

	public final String getServerMessage(String... replacements) {
		return getMessage(Locale.ENGLISH, replacements);
	}

	public final String getMessage(Player user, String... replacements) {
		return getMessage(I18n.getPlayerLanguage(user), replacements);
	}
}