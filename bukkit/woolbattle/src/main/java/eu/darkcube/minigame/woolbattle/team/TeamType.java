package eu.darkcube.minigame.woolbattle.team;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.configuration.file.YamlConfiguration;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer.DontSerialize;
import eu.darkcube.minigame.woolbattle.util.Serializable;

public class TeamType implements Comparable<TeamType>, Serializable {

	private static final Collection<TeamType> TYPES = new HashSet<>();
	public static TeamType SPECTATOR;

	private final String invisibleTag;
	private final String scoreboardTag;
	private final String displayNameKey;
	private boolean enabled;
	private byte woolcolor;
	@DontSerialize
	private DyeColor woolcolorDye;
	private char namecolor;
	private int maxPlayers;
	private final int weight;
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
			builder.append(ChatColor.DARK_BLUE.toString());
		}
		builder.append(ChatColor.DARK_RED.toString());
		this.scoreboardTag = this.weight + "I" + index;
		this.invisibleTag = builder.toString();
		TYPES.add(this);
	}

	public void save() {
		TYPES.add(this);
		YamlConfiguration cfg = WoolBattle.getInstance().getConfig("teams");
		List<String> teams = cfg.getStringList("teams");
		teams.add(serialize());
		cfg.set("teams", teams);
		WoolBattle.getInstance().saveConfig(cfg);
	}

	public boolean isDeleted() {
		return !WoolBattle.getInstance().getConfig("teams").getStringList("teams")
				.contains(serialize());
	}

	public void delete() {
		YamlConfiguration cfg = WoolBattle.getInstance().getConfig("teams");
		List<String> teams = cfg.getStringList("teams");
		teams.remove(serialize());
		cfg.set("teams", teams);
		TYPES.remove(this);
		WoolBattle.getInstance().saveConfig(cfg);
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

	public String getDisplayNameKey() {
		return displayNameKey;
	}

	public int getIndex() {
		return index;
	}

	public void setEnabled(boolean enabled) {
		delete();
		this.enabled = enabled;
		save();
	}

	public void setMaxPlayers(int maxPlayers) {
		delete();
		this.maxPlayers = maxPlayers;
		save();
	}

	public void setNameColor(ChatColor namecolor) {
		delete();
		this.namecolor = namecolor.getChar();
		save();
	}

	@SuppressWarnings("deprecation")
	public void setWoolColor(DyeColor woolcolor) {
		delete();
		this.woolcolorDye = woolcolor;
		this.woolcolor = woolcolor.getData();
		save();
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

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public char getNameColor() {
		return namecolor;
	}

	public String getScoreboardTag() {
		return scoreboardTag;
	}

	public int getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return getDisplayNameKey();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TeamType)) {
			return false;
		}
		TeamType o = (TeamType) obj;
		if (o.enabled == enabled && o.displayNameKey.equals(displayNameKey) && o.index == index
				&& o.invisibleTag.equals(invisibleTag) && o.maxPlayers == maxPlayers
				&& o.namecolor == namecolor && o.scoreboardTag.equals(scoreboardTag)
				&& o.weight == weight && o.woolcolor == woolcolor) {
			return true;
		}
		return false;
	}

	public static final TeamType byDisplayNameKey(String displayNameKey) {
		for (TeamType type : TYPES) {
			if (type.displayNameKey.equals(displayNameKey)) {
				return type;
			}
		}
		return null;
	}

	public static final TeamType deserialize(String json) {
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

	public static final TeamType[] validValues() {
		Collection<TeamType> types = Arrays.asList(values());
		types = types.stream().filter(t -> t.isEnabled()).collect(Collectors.toSet());
		return types.toArray(new TeamType[0]);
	}

	public static final TeamType[] values() {
		return TYPES.toArray(new TeamType[0]);
	}

	public static Collection<TeamType> getTypes() {
		return TYPES;
	}

	@Override
	public int compareTo(TeamType o) {
		return -((Integer) o.getWeight()).compareTo(weight);
	}
}
