/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.stats;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.stats.api.Arrays;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.gamemode.GameMode;
import eu.darkcube.system.stats.api.user.User;
import eu.darkcube.system.util.ChatBaseComponent;
import eu.darkcube.system.util.ChatUtils.ChatEntry;
import eu.darkcube.system.util.ChatUtils.ChatEntry.Builder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
		return this.gamemode;
	}

	public Duration getDuration() {
		return this.duration;
	}

	public User getOwner() {
		return this.owner;
	}

	public final List<ChatEntry> format() {
		List<ChatEntry> list = new ArrayList<>();
		list.addAll(Arrays.asList(
				new Builder().text("§7» §5Wool§dBattle\n §7Statistiken von §a" + this.owner.getName() + "\n").build()));
		this.insertBreakLine(list);
		this.insertFormats(list);
		this.insertBreakLine(list);

		return list;
	}

	public final ChatBaseComponent formatComponent() {
		return ChatEntry.build(this.format().toArray(new ChatEntry[0]));
	}

	protected abstract void insertFormats(List<ChatEntry> list);

	protected final void insertBreakLine(List<ChatEntry> list) {
		list.addAll(Arrays.asList(new ChatEntry.Builder().text("§8------------------------\n").build()));
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
