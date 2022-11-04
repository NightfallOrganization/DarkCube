package eu.darkcube.minigame.smash.user;

import org.bukkit.ChatColor;

public enum Message {
	INVENTORY_TITLE_VOTING_LIFES, VOTED_FOR_LIFES, INVENTORY_TITLE_VOTING_MAPS, VOTED_FOR_MAP

	;

	public static final String ITEM_PREFIX = "ITEM_";
	public static final String LORE_PREFIX = "LORE_";
	public static final String TEAM_PREFIX = "TEAM_";

	private final String key;

	Message() {
		this.key = this.name();
	}

	public static final String getMessage(String messageKey, Language language, String... replacements) {
		try {
			String msg = language.getBundle().getString(messageKey);
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

	public final String getMessage(Language language, String... replacements) {
		return getMessage(key, language, replacements);
	}

	public final String getServerMessage(String... replacements) {
		return getMessage(Language.ENGLISH, replacements);
	}

	public final String getMessage(User user, String... replacements) {
		return getMessage(user.getLanguage(), replacements);
	}
}