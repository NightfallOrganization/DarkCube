package eu.darkcube.system.cloudban.util.ban;

import java.util.*;

import eu.darkcube.system.cloudban.util.*;

public class Ban {

	public static final UUID CONSOLE = UUID.fromString("00000000-0000-0000-0000-000000000000");
	public static final String BAN_MESSAGE = "&5&lDark&d&lCube&8.&5eu\n" + "&cDu wurdest vom Netzwerk gebannt!\n" + "\n"
			+ "&7Grund: &6%reason%\n" + "&7Gebannt am: &b%bannedat%\n" + "&7Verbleibende Zeit: &b%timeremain%\n" + "\n"
			+ "&7Du kannst unter &ehttps://darkcube.eu/forum/ &7einen\n" + "&7Entbannungsantrag stellen!";
	public static final String MUTE_MESSAGE = "&5&lDark&d&lCube&8.&5eu\n" + "&cDu wurdest gemutet!\n"
			+ "&7Grund: &6%reason%\n" + "&7Verbleibende Zeit: &b%timeremain%\n"
			+ "&7Du kannst unter &ehttps://darkcube.eu/forum/ &7einen Entbannungsantrag stellen!";

	private UUID uuid;
	private DateTime timeBanned;
	private Duration duration;
	private Server server;
	private BanType type;
	private Reason reason;
	private UUID bannedBy;

	public Ban(UUID uuid, DateTime timeBanned, Duration duration, Server server, BanType type, Reason reason,
			UUID bannedBy) {
		this.uuid = uuid;
		this.timeBanned = timeBanned;
		this.duration = duration;
		this.server = server;
		this.type = type;
		this.reason = reason;
		this.bannedBy = bannedBy;
	}

	public DateTime getTimeBanned() {
		return timeBanned;
	}

	public Duration getDuration() {
		return duration;
	}

	public Server getServer() {
		return server;
	}

	public UUID getUUID() {
		return uuid;
	}

	public BanType getBanType() {
		return type;
	}

	public Reason getReason() {
		return reason;
	}

	public UUID getBannedBy() {
		return bannedBy;
	}

	public String getPlayerNameBannedBy() {
		if (bannedBy.equals(CONSOLE))
			return "Console";
		return UUIDManager.getPlayerName(getBannedBy());
	}

	public boolean isActive() {
		TimeHolder expires = new TimeHolder(timeBanned.getYear().add(duration.getYear()),
				timeBanned.getMonth().add(duration.getMonth()), timeBanned.getDay().add(duration.getDay()),
				timeBanned.getHour().add(duration.getHour()), timeBanned.getMinute().add(duration.getMinute()),
				timeBanned.getSecond().add(duration.getSecond()));
		DateTime current = DateTime.current();
//		if (expires.getYear().compareTo(current.getYear().add(BigInteger.ONE)) == -1
//				&& expires.getMonth().compareTo(current.getMonth().add(BigInteger.ONE)) == -1
//				&& expires.getDay().compareTo(current.getDay().add(BigInteger.ONE)) == -1
//				&& expires.getHour().compareTo(current.getHour().add(BigInteger.ONE)) == -1
//				&& expires.getMinute().compareTo(current.getMinute().add(BigInteger.ONE)) == -1
//				&& expires.getSecond().compareTo(current.getSecond().add(BigInteger.ONE)) == -1) {
//			return false;
//		}
		if(expires.compareTo(current) == -1) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return BanUtil.GSON.toJson(this);
	}

	public static Ban deserialize(String json) {
		return BanUtil.GSON.fromJson(json, Ban.class);
	}

	public String serialize() {
		return toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Ban))
			return false;
		Ban b = (Ban) obj;
		if (b.getBanType().equals(type) && b.getTimeBanned().equals(timeBanned) && b.getDuration().equals(duration)
				&& b.getReason().equals(reason) && b.getServer().equals(server) && b.getBannedBy().equals(bannedBy))
			return true;
		return false;
	}
}
