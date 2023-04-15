/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.team;

import com.google.gson.Gson;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer.DontSerialize;
import eu.darkcube.minigame.woolbattle.util.Serializable;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TeamType implements Comparable<TeamType>, Serializable {

	private static final Collection<TeamType> TYPES = new HashSet<>();
	public static TeamType SPECTATOR;

	private final String invisibleTag;
	private final String scoreboardTag;
	private final String displayNameKey;
	private final int weight;
	private boolean enabled;
	private byte woolcolor;
	@DontSerialize
	private DyeColor woolcolorDye;
	private char namecolor;
	private int maxPlayers;
	@DontSerialize
	private int index;

	@SuppressWarnings("deprecation")
	public TeamType(String displayNameKey, int weight, byte woolcolor, ChatColor namecolor,
			boolean enabled, int maxPlayers) {
		this.displayNameKey = displayNameKey;
		this.woolcolor = woolcolor;
		this.woolcolorDye = DyeColor.getByData(woolcolor);
		this.namecolor = namecolor.getChar();
		this.maxPlayers = maxPlayers;
		this.enabled = enabled;
		this.weight = weight;
		this.index = index(true);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < this.index; i++) {
			builder.append(ChatColor.DARK_BLUE);
		}
		builder.append(ChatColor.DARK_RED);
		this.scoreboardTag = this.weight + "I" + index;
		this.invisibleTag = builder.toString();
		TYPES.add(this);
	}

	public static TeamType byDisplayNameKey(String displayNameKey) {
		for (TeamType type : TYPES) {
			if (type.displayNameKey.equals(displayNameKey)) {
				return type;
			}
		}
		return null;
	}

	public static TeamType deserialize(String json) {
		for (TeamType type : TYPES) {
			if (type.serialize().equals(json)) {
				return type;
			}
		}
		TeamType type = new Gson().fromJson(json, TeamType.class);
		type.index(true);
		TYPES.add(type);
		return type;
	}

	public static TeamType[] validValues() {
		Collection<TeamType> types = Arrays.asList(values());
		types = types.stream().filter(TeamType::isEnabled).collect(Collectors.toSet());
		return types.toArray(new TeamType[0]);
	}

	public static TeamType[] values() {
		return TYPES.toArray(new TeamType[0]);
	}

	public static Collection<TeamType> getTypes() {
		return TYPES;
	}

	public void save() {
		TYPES.add(this);
		YamlConfiguration cfg = WoolBattle.instance().getConfig("teams");
		List<String> teams = cfg.getStringList("teams");
		teams.add(serialize());
		cfg.set("teams", teams);
		WoolBattle.instance().saveConfig(cfg);
	}

	public boolean isDeleted() {
		return !WoolBattle.instance().getConfig("teams").getStringList("teams")
				.contains(serialize());
	}

	public void delete() {
		YamlConfiguration cfg = WoolBattle.instance().getConfig("teams");
		List<String> teams = cfg.getStringList("teams");
		teams.remove(serialize());
		cfg.set("teams", teams);
		TYPES.remove(this);
		WoolBattle.instance().saveConfig(cfg);
	}

	private int index(boolean flag) {
		if (flag)
			this.index = 0;
		for (TeamType type : TYPES) {
			if (type.index == index) {
				index++;
				return index(false);
			}
		}
		return this.index;
	}

	public byte getWoolColorByte() {
		return woolcolor;
	}

	@SuppressWarnings("deprecation")
	public DyeColor getWoolColor() {
		if (woolcolorDye == null) {
			woolcolorDye = DyeColor.getByData(woolcolor);
		}
		return woolcolorDye;
	}

	@SuppressWarnings("deprecation")
	public void setWoolColor(DyeColor woolcolor) {
		delete();
		this.woolcolorDye = woolcolor;
		this.woolcolor = woolcolor.getData();
		save();
	}

	public String getDisplayNameKey() {
		return displayNameKey;
	}

	public int getIndex() {
		return index;
	}

	public String getInvisibleTag() {
		return invisibleTag;
	}

	public String getIngameScoreboardTag() {
		return "ig" + getWeight();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		delete();
		this.enabled = enabled;
		save();
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		delete();
		this.maxPlayers = maxPlayers;
		save();
	}

	public char getNameColor() {
		return namecolor;
	}

	public void setNameColor(ChatColor namecolor) {
		delete();
		this.namecolor = namecolor.getChar();
		save();
	}

	public String getScoreboardTag() {
		return scoreboardTag;
	}

	public int getWeight() {
		return weight;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TeamType)) {
			return false;
		}
		TeamType o = (TeamType) obj;
		return o.enabled == enabled && o.displayNameKey.equals(displayNameKey) && o.index == index
				&& o.invisibleTag.equals(invisibleTag) && o.maxPlayers == maxPlayers
				&& o.namecolor == namecolor && o.scoreboardTag.equals(scoreboardTag)
				&& o.weight == weight && o.woolcolor == woolcolor;
	}

	@Override
	public String toString() {
		return getDisplayNameKey();
	}

	@Override
	public int compareTo(TeamType o) {
		return -Integer.compare(o.getWeight(), weight);
	}
}
