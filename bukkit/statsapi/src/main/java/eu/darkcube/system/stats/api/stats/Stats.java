package eu.darkcube.system.stats.api.stats;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.ChatBaseComponent;
import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.ChatUtils.ChatEntry.Builder;
import eu.darkcube.system.stats.api.Arrays;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.gamemode.GameMode;
import eu.darkcube.system.stats.api.user.User;

public abstract class Stats {

	private final Duration duration;
	private final GameMode gamemode;
	private final User owner;

	public Stats(User owner, Duration duration, GameMode gamemode) {
		this.gamemode = gamemode;
		this.duration = duration;
		this.owner = owner;
	}

	public GameMode getGamemode() {
		return gamemode;
	}

	public Duration getDuration() {
		return duration;
	}

	public User getOwner() {
		return owner;
	}

	public final List<ChatEntry> format() {
		List<ChatEntry> list = new ArrayList<>();
//		StringBuilder builder = new StringBuilder();
		list.addAll(Arrays.asList(new Builder().text("§7» §5Wool§dBattle\n §7Statistiken von §a" + owner.getName()).build()));
//		builder.append("&7» &5Wool&dBattle &7Statistiken von \n&a").append(getOwner().getName()).append("&7 (")
//				.append(getDuration().format()).append("&7)\n");
		insertBreakLine(list);
		insertFormats(list);
		insertBreakLine(list);

		return list;
	}
	
	public final ChatBaseComponent formatComponent() {
		return ChatEntry.buildArray(format().toArray(new ChatEntry[0]));
	}

	protected abstract void insertFormats(List<ChatEntry> list);

	protected final void insertBreakLine(List<ChatEntry> list) {
		list.addAll(Arrays.asList(new ChatEntry.Builder().text("§8------------------------").build()));
	}

	public static final String format(String key, String user, long placement) {
		return "§7" + key + "§7: §6" + user + " §f» §7(Platz " + placement + ")";
	}

	public static final String formatToplist(String user, long placement, String value) {
		return "§a" + placement + "§7: §5" + user + " §7(§6" + value + "§7)";
	}

	public abstract JsonDocument serializeData();

	public abstract void loadData(@Nullable JsonDocument document);

}