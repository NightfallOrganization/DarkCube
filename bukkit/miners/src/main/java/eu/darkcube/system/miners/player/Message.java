package eu.darkcube.system.miners.player;

import java.util.function.Function;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.miners.Miners;

// copied from woolbattle
public enum Message {

	SERVER_STOP, TIME_REMAINING, TIME_SECONDS, TIME_MINUTES, PLAYER_JOINED, PLAYER_LEFT, COMMAND_TIMER_CHANGED

	;

	public static final String KEY_PREFIX = "MINERS_";
	public static final Function<String, String> KEY_MODFIIER = s -> KEY_PREFIX + s;
	public static final String ITEM_PREFIX = "ITEM_";
	public static final String LORE_PREFIX = "LORE_";

	private final String key;

	Message() {
		this.key = this.name();
	}

	public String getKey() {
		return key;
	}

	public static final String getMessage(String messageKey, Language language, Object... replacements) {
		return ChatColor.translateAlternateColorCodes('&', language.getMessage(KEY_PREFIX + messageKey, replacements));
	}

	public final String getMessage(Language language, Object... replacements) {
		return getMessage(key, language, replacements);
	}

	public final String getServerMessage(Object... replacements) {
//		return getMessage(Main.getInstance().getServerLanguage(), replacements);
//		return Language.ENGLISH.getMessage(key, replacements);
		return getMessage(key, Language.ENGLISH, replacements);
	}

	public final String getMessage(MinersPlayer user, Object... replacements) {
		return getMessage(user.getLanguage(), replacements);
	}

	public final String getMessage(Player user, Object... replacements) {
		return getMessage(Miners.getPlayerManager().getMinersPlayer(user).getLanguage(), replacements);
	}

}
