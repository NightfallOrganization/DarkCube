/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.mysql;

import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.StatsPlugin;
import eu.darkcube.system.stats.api.gamemode.GameMode;
import eu.darkcube.system.stats.api.gamemode.StatsWoolBattle;
import eu.darkcube.system.stats.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQL {

	private static final Collection<String> TABLES = new ArrayList<>();

	public static MySQL mysql;
	private static int emptyCount = 0;

	private Connection con;
	private final String host;
	private final String port;
	private final String database;
	private final String username;
	private final String password;

	public static final String WOOLBATTLE_BASE_NAME = "stats_woolbattle";
	public static final String USER_LAST_GAMEMODE_NAME = "stats_user_last_gamemode";

	public static final String[] BASE_NAMES = new String[] {WOOLBATTLE_BASE_NAME};

	static {
		TABLES.add("CREATE TABLE IF NOT EXISTS " + USER_LAST_GAMEMODE_NAME + " (uuid VARCHAR("
				+ UUID.randomUUID().toString().length() + ") PRIMARY KEY, value TEXT)");
		addTables("uuid VARCHAR(" + UUID.randomUUID().toString().length()
				+ "), elo DOUBLE, kills BIGINT, deaths BIGINT, killDeathRatio DOUBLE, wins BIGINT, losses BIGINT, "
				+ "winLossRatio DOUBLE, PRIMARY KEY(uuid)");
	}

	public static String getTableName(String basename, Duration duration) {
		return basename + ("_" + duration.format()).replace("_" + Duration.ALLTIME.format(), "");
	}

	private static void addTables(String data) {
		for (Duration duration : Duration.values()) {
			addTable(duration, data);
		}
	}

	private static void addTable(Duration duration, String data) {
		TABLES.add(
				"CREATE TABLE IF NOT EXISTS " + getTableName(MySQL.WOOLBATTLE_BASE_NAME, duration)
						+ " (" + data + ")");
	}

	public static void setLastGameMode(User user, GameMode gamemode) {
		setObject(USER_LAST_GAMEMODE_NAME, "value", user.getUniqueId().toString(), gamemode.name());
	}

	public static GameMode getLastGameMode(User user) {
		Object o = getObject(USER_LAST_GAMEMODE_NAME, "value", user.getUniqueId().toString());
		return o == null ? GameMode.WOOLBATTLE : GameMode.valueOf(o.toString());
	}

	public static void setStatsWoolBattle(StatsWoolBattle stats) {
		setWoolBattleKills(stats.getOwner(), stats.getDuration(), stats.getKills());
		setWoolBattleDeaths(stats.getOwner(), stats.getDuration(), stats.getDeaths());
		setWoolBattleKillDeathRatio(stats.getOwner(), stats.getDuration(),
				stats.getKillDeathRatio());
		setWoolBattleWins(stats.getOwner(), stats.getDuration(), stats.getWins());
		setWoolBattleLosses(stats.getOwner(), stats.getDuration(), stats.getLosses());
		setWoolBattleWinLossRatio(stats.getOwner(), stats.getDuration(), stats.getWinLossRatio());
		setWoolBattleElo(stats.getOwner(), stats.getDuration(), stats.getElo());
	}

	public static StatsWoolBattle getStatsWoolBatte(User owner, Duration duration) {
		GameMode gamemode = GameMode.WOOLBATTLE;
		long kills = getWoolBattleKills(owner, duration);
		long placementKills = getWoolBattlePlacementKills(owner, duration);
		long deaths = getWoolBattleDeaths(owner, duration);
		long placementDeaths = getWoolBattlePlacementDeaths(owner, duration);
		long placementKillDeathRatio = getWoolBattlePlacementKillDeathRatio(owner, duration);
		long wins = getWoolBattleWins(owner, duration);
		long placementWins = getWoolBattlePlacementWins(owner, duration);
		long losses = getWoolBattleLosses(owner, duration);
		long placementLosses = getWoolBattlePlacementLosses(owner, duration);
		long placementWinLossRatio = getWoolBattlePlacementWinLossRatio(owner, duration);
		double elo = getWoolBattleElo(owner, duration);
		long placementElo = getWoolBattlePlacementElo(owner, duration);
		return new StatsWoolBattle(owner, duration, gamemode, kills, deaths, placementKills,
				placementDeaths, placementKillDeathRatio, wins, losses, placementWins,
				placementLosses, placementWinLossRatio, elo, placementElo);
	}

	public static void setWoolBattleWinLossRatio(User user, Duration duration, double ratio) {
		if (!Double.isFinite(ratio) || Double.isNaN(ratio)) {
			ratio = getWoolBattleKills(user, duration);
		}
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "winLossRatio",
				user.getUniqueId().toString(), ratio);
	}

	public static void setWoolBattleKillDeathRatio(User user, Duration duration, double ratio) {
		if (!Double.isFinite(ratio) || Double.isNaN(ratio)) {
			ratio = getWoolBattleKills(user, duration);
		}
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "killDeathRatio",
				user.getUniqueId().toString(), ratio);
	}

	public static void setWoolBattleWins(User user, Duration duration, long wins) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "wins",
				user.getUniqueId().toString(), wins);
		setWoolBattleWinLossRatio(user, duration,
				(double) wins / (double) getWoolBattleLosses(user, duration));
	}

	public static void setWoolBattleLosses(User user, Duration duration, long losses) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "losses",
				user.getUniqueId().toString(), losses);
		setWoolBattleWinLossRatio(user, duration,
				(double) getWoolBattleWins(user, duration) / (double) losses);
	}

	public static void setWoolBattleElo(User user, Duration duration, double elo) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "elo",
				user.getUniqueId().toString(), elo);
	}

	public static void setWoolBattleKills(User user, Duration duration, long kills) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "kills",
				user.getUniqueId().toString(), kills);
		double deaths = getWoolBattleDeaths(user, duration);
		setWoolBattleKillDeathRatio(user, duration, deaths == 0 ? kills : kills / deaths);
	}

	public static void setWoolBattleDeaths(User user, Duration duration, long deaths) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "deaths",
				user.getUniqueId().toString(), deaths);
		setWoolBattleKillDeathRatio(user, duration, deaths == 0
				? (double) getWoolBattleKills(user, duration)
				: (double) getWoolBattleKills(user, duration) / (double) deaths);
	}

	public static double getWoolBattleWinLossRatio(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "winLossRatio",
				user.getUniqueId().toString());
		return o == null ? 0 : (double) o;
	}

	public static long getWoolBattlePlacementWinLossRatio(User user, Duration duration) {
		setWoolBattleWinLossRatio(user, duration,
				(double) getWoolBattleWins(user, duration) / (double) getWoolBattleLosses(user,
						duration));
		if (getWoolBattleWinLossRatio(user, duration) == 0) {
			return -1;
		}
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "winLossRatio");
	}

	public static double getWoolBattleKillDeathRatio(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "killDeathRatio",
				user.getUniqueId().toString());
		return o == null ? 0 : (double) o;
	}

	public static long getWoolBattlePlacementKillDeathRatio(User user, Duration duration) {
		setWoolBattleKillDeathRatio(user, duration,
				(double) getWoolBattleKills(user, duration) / (double) getWoolBattleDeaths(user,
						duration));
		if (getWoolBattleKillDeathRatio(user, duration) == 0) {
			return -1;
		}
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "killDeathRatio");
	}

	public static long getWoolBattleWins(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "wins",
				user.getUniqueId().toString());
		return o == null ? 0 : (long) o;
	}

	public static long getWoolBattlePlacementWins(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "wins");
	}

	public static long getWoolBattleLosses(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "losses",
				user.getUniqueId().toString());
		return o == null ? 0 : (long) o;
	}

	public static long getWoolBattlePlacementLosses(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "losses");
	}

	public static long getWoolBattleDeaths(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "deaths",
				user.getUniqueId().toString());
		return o == null ? 0 : (long) o;
	}

	public static long getWoolBattlePlacementDeaths(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "deaths");
	}

	public static long getWoolBattleKills(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "kills",
				user.getUniqueId().toString());
		return o == null ? 0 : (long) o;
	}

	public static long getWoolBattlePlacementKills(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "kills");
	}

	public static double getWoolBattleElo(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "elo",
				user.getUniqueId().toString());
		return o == null ? 2000 : (double) o;
	}

	public static long getWoolBattlePlacementElo(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "elo");
	}

	private static long getPlacement(User user, String table, String column) {
		try {
			Result res = mysql.getResult("SELECT COUNT(*) FROM {} "
							+ "WHERE {} >= (SELECT {} FROM stats_woolbattle WHERE uuid='{}') ORDER BY {}, uuid",
					table, column, column, user.getUniqueId().toString(), column);
			if (!res.isEmpty() && (long) res.getObject() != 0) {
				return (long) res.getObject();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	private static void insert(String table, String key) {
		String[] columns = getColumnNames(table);
		if (columns.length != 0) {
			try {
				StringBuilder b = new StringBuilder();
				b.append("'").append(key).append("'");
				for (int i = 1; i < columns.length; i++) {
					b.append(", ").append("NULL");
				}
				b.append(')');
				PreparedStatement s =
						mysql.con.prepareStatement("INSERT INTO " + table + " VALUES (" + b);
				s.executeUpdate();
				s.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static Object getObject(String table, String columnName, String key) {
		try {
			Result res =
					mysql.getResult("SELECT {} FROM {} WHERE {}='{}'", columnName, table, "uuid",
							key);
			if (!res.isEmpty()) {
				return res.getObject();
			}
			insert(table, key);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static void setObject(String table, String columnName, String key, Object object) {
		getObject(table, columnName, key);
		mysql.update("UPDATE {} SET {}='{}' WHERE {}='{}'", table, columnName, object.toString(),
				"uuid", key);
	}

	public MySQL(String host, String port, String database, String username, String password) {
		if (mysql != null)
			throw new IllegalAccessError("MySQL service is already running!");
		mysql = this;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public final void connect() {
		if (!isConnected()) {
			try {
				con = DriverManager.getConnection(
						"jdbc:mysql://" + host + ":" + port + "/" + database
								+ "?characterEncoding=utf8", username, password);
				TABLES.forEach(this::update);
				StatsPlugin.getInstance().sendConsole("[MySQL] Connected!");
			} catch (SQLException ex) {
				//				if (ex instanceof CommunicationsException) {
				//					StatsPlugin.getInstance()
				//							.sendConsole(ChatColor.RED + "Could not connect to database. Shutting down server. "
				//									+ "\nPlease ensure your IP in the config is correct and the MySQL database is online!");
				//					loadError();
				//				} else if (ex instanceof MySQLSyntaxErrorException) {
				//					StatsPlugin.getInstance()
				//							.sendConsole(ChatColor.RED + "Could not connect to database. Shutting down server. "
				//									+ "\nPlease ensure your values in the config are correct, such like password, username.\n"
				//									+ ex.getMessage());
				//					loadError();
				//				} else
				if (ex.getMessage().startsWith("Access denied for user")) {
					StatsPlugin.getInstance().sendMessage(
							ChatColor.RED + "Could not connect to database. Shutting down server. "
									+ "\nThe entered password is wrong!\n" + ex.getMessage());
					loadError();
				} else {
					//					handleException(ex);
					loadError();
				}
			}
		}
	}

	public final void disconnect() {
		if (isConnected()) {
			try {
				con.close();
				StatsPlugin.getInstance().sendConsole("[MySQL] Disconnected!");
			} catch (SQLException ex) {
				handleException();
			}
		}
	}

	public final boolean isConnected() {
		try {
			return con != null && !con.isClosed();
		} catch (SQLException ex) {
			return false;
		}
	}

	public final Result getResult(final String query, final CharSequence... replace) {
		if (!isConnected()) {
			disconnect();
			connect();
		}
		String[] replacements = new String[replace.length];
		for (int i = 0; i < replace.length; i++) {
			replacements[i] = replace[i].toString();
		}

		ResultSet res = null;
		try {
			PreparedStatement ps = con.prepareStatement(format(query, replacements));
			res = ps.executeQuery();
			boolean empty = res.first();
			if (empty && emptyCount <= 0 && !isConnected()) {
				emptyCount++;
				disconnect();
				connect();
				return getResult(query, replace);
			}
			emptyCount = 0;
			res.next();
		} catch (SQLException e) {
			handleException();
			return getResult(query, replace);
		}

		return new MySQLResult(this, res);
	}

	public final void update(String query, final CharSequence... replace) {
		String[] replacements = new String[replace.length];
		for (int i = 0; i < replace.length; i++) {
			replacements[i] = replace[i].toString();
		}
		try {
			PreparedStatement ps = con.prepareStatement(format(query, replacements));
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			handleException();
		}
	}

	public void loadError() {
		try {
			StatsPlugin.getInstance().sendMessage(ChatColor.DARK_RED + "Stopping server!");
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		Bukkit.shutdown();
	}

	public final String toString(ResultSet res) throws SQLException {
		int startIndex = res.getRow();
		StringBuilder builder = new StringBuilder("");
		List<String> keysAsList = new ArrayList<>();
		for (int i = 0; i < res.getMetaData().getColumnCount(); i++) {
			keysAsList.add(res.getMetaData().getColumnName(i + 1));
		}
		String[] keys = keysAsList.toArray(new String[] {});
		if (res.first()) {
			appendRow(builder, res, keys);
			for (int i = 1; res.next() && keys.length > i; i++)
				appendRow(builder, res, keys);
			res.first();
			for (int i = 0; i < startIndex && res.next(); i++)
				;
		}
		return builder.toString();
	}

	private static String format(String msg, final String... replacements) {
		int i = 0;
		for (int j = 0; j < replacements.length; j++) {
			if (replacements[i].equals("true"))
				replacements[i] = "1";
			else if (replacements[i].equals("false"))
				replacements[i] = "0";
		}
		for (; msg.contains("{}") && replacements.length > i; i++)
			//			msg = msg.replaceFirst("\\{\\}", replacements[i]);
			msg = Pattern.compile("\\{}").matcher(msg)
					.replaceFirst(Matcher.quoteReplacement(replacements[i]));
		return msg;
	}

	private static String[] getColumnNames(String table) {
		List<String> res = new ArrayList<>();
		try {
			ResultSet r = mysql.getResult("SELECT * FROM " + table).raw();
			for (int i = 0; i < r.getMetaData().getColumnCount(); i++)
				res.add(r.getMetaData().getColumnName(i + 1));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return res.toArray(new String[0]);
	}

	private void handleException() throws RuntimeException {
		disconnect();
		connect();
	}

	private String getRow(ResultSet res, String... keys) throws SQLException {
		StringBuilder builder = new StringBuilder().append("Entry[");
		for (int i = 0; i < keys.length; i++) {
			builder.append(res.getMetaData().getColumnName(i + 1)).append('=')
					.append(res.getObject(keys[i])).append(", ");
		}
		return builder.substring(0, builder.length() - 2) + ']';
	}

	private void appendRow(StringBuilder builder, ResultSet res, String... keys)
			throws SQLException {
		builder.append(getRow(res, keys));
	}

}
