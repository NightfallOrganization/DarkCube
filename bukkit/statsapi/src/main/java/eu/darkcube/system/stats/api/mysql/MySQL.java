package eu.darkcube.system.stats.api.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.StatsPlugin;
import eu.darkcube.system.stats.api.gamemode.GameMode;
import eu.darkcube.system.stats.api.gamemode.StatsWoolBattle;
import eu.darkcube.system.stats.api.user.User;

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

	public static final String[] BASE_NAMES = new String[] { WOOLBATTLE_BASE_NAME };

	static {
//		TABLES.add("CREATE TABLE IF NOT EXISTS stats_woolbattle (uuid VARCHAR(" + UUID.randomUUID().toString().length()
//				+ "), elo BIGINT, kills BIGINT, deaths BIGINT, killDeathRatio DOUBLE, wins BIGINT, losses BIGINT, "
//				+ "winLossRatio DOUBLE, PRIMARY KEY(uuid))");
		TABLES.add("CREATE TABLE IF NOT EXISTS " + USER_LAST_GAMEMODE_NAME + " (uuid VARCHAR("
				+ UUID.randomUUID().toString().length() + ") PRIMARY KEY, value TEXT)");
		addTables(WOOLBATTLE_BASE_NAME, "uuid VARCHAR(" + UUID.randomUUID().toString().length()
				+ "), elo DOUBLE, kills BIGINT, deaths BIGINT, killDeathRatio DOUBLE, wins BIGINT, losses BIGINT, "
				+ "winLossRatio DOUBLE, PRIMARY KEY(uuid)");
	}

	public static final String getTableName(String basename, Duration duration) {
		return basename + ("_" + duration.format()).replace("_" + Duration.ALLTIME.format(), "");
	}

	private static final void addTables(String basename, String data) {
		for (Duration duration : Duration.values()) {
			addTable(basename, duration, data);
		}
	}

	private static final void addTable(String basename, Duration duration, String data) {
		TABLES.add("CREATE TABLE IF NOT EXISTS " + getTableName(basename, duration) + " (" + data + ")");
	}

	public static final void setLastGameMode(User user, GameMode gamemode) {
		setObject(USER_LAST_GAMEMODE_NAME, "value", "uuid", user.getUniqueId().toString(), gamemode.name());
	}

	public static final GameMode getLastGameMode(User user) {
		Object o = getObject(USER_LAST_GAMEMODE_NAME, "value", "uuid", user.getUniqueId().toString());
		return o == null ? GameMode.WOOLBATTLE : GameMode.valueOf(o.toString());
	}

	public static final void setStatsWoolBattle(StatsWoolBattle stats) {
		setWoolBattleKills(stats.getOwner(), stats.getDuration(), stats.getKills());
		setWoolBattleDeaths(stats.getOwner(), stats.getDuration(), stats.getDeaths());
		setWoolBattleKillDeathRatio(stats.getOwner(), stats.getDuration(), stats.getKillDeathRatio());
		setWoolBattleWins(stats.getOwner(), stats.getDuration(), stats.getWins());
		setWoolBattleLosses(stats.getOwner(), stats.getDuration(), stats.getLosses());
		setWoolBattleWinLossRatio(stats.getOwner(), stats.getDuration(), stats.getWinLossRatio());
		setWoolBattleElo(stats.getOwner(), stats.getDuration(), stats.getElo());
	}

	public static final StatsWoolBattle getStatsWoolBatte(User owner, Duration duration) {
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
		return new StatsWoolBattle(owner, duration, gamemode, kills, deaths, placementKills, placementDeaths,
				placementKillDeathRatio, wins, losses, placementWins, placementLosses, placementWinLossRatio, elo,
				placementElo);
	}

	public static final void setWoolBattleWinLossRatio(User user, Duration duration, double ratio) {
		if (!Double.isFinite(ratio) || ratio == Double.NaN) {
			ratio = getWoolBattleKills(user, duration);
		}
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "winLossRatio", "uuid", user.getUniqueId().toString(),
				ratio);
	}

	public static final void setWoolBattleKillDeathRatio(User user, Duration duration, double ratio) {
		if (!Double.isFinite(ratio) || ratio == Double.NaN) {
			ratio = getWoolBattleKills(user, duration);
		}
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "killDeathRatio", "uuid", user.getUniqueId().toString(),
				ratio);
	}

	public static final void setWoolBattleWins(User user, Duration duration, long wins) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "wins", "uuid", user.getUniqueId().toString(), wins);
		setWoolBattleWinLossRatio(user, duration, (double) wins / (double) getWoolBattleLosses(user, duration));
	}

	public static final void setWoolBattleLosses(User user, Duration duration, long losses) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "losses", "uuid", user.getUniqueId().toString(),
				losses);
		setWoolBattleWinLossRatio(user, duration, (double) getWoolBattleWins(user, duration) / (double) losses);
	}

	public static final void setWoolBattleElo(User user, Duration duration, double elo) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "elo", "uuid", user.getUniqueId().toString(), elo);
	}

	public static final void setWoolBattleKills(User user, Duration duration, long kills) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "kills", "uuid", user.getUniqueId().toString(), kills);
		double deaths = getWoolBattleDeaths(user, duration);
		setWoolBattleKillDeathRatio(user, duration, deaths == 0 ? kills : kills / deaths);
	}

	public static final void setWoolBattleDeaths(User user, Duration duration, long deaths) {
		setObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "deaths", "uuid", user.getUniqueId().toString(),
				deaths);
		setWoolBattleKillDeathRatio(user, duration, deaths == 0 ? (double) getWoolBattleKills(user, duration)
				: (double) getWoolBattleKills(user, duration) / (double) deaths);
	}

	public static final double getWoolBattleWinLossRatio(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "winLossRatio", "uuid",
				user.getUniqueId().toString());
		return o == null ? 0 : (double) o;
	}

	public static final long getWoolBattlePlacementWinLossRatio(User user, Duration duration) {
		setWoolBattleWinLossRatio(user, duration,
				(double) getWoolBattleWins(user, duration) / (double) getWoolBattleLosses(user, duration));
		if (getWoolBattleWinLossRatio(user, duration) == 0) {
			return -1;
		}
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "winLossRatio");
	}

	public static final double getWoolBattleKillDeathRatio(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "killDeathRatio", "uuid",
				user.getUniqueId().toString());
		return o == null ? 0 : (double) o;
	}

	public static final long getWoolBattlePlacementKillDeathRatio(User user, Duration duration) {
		setWoolBattleKillDeathRatio(user, duration,
				(double) getWoolBattleKills(user, duration) / (double) getWoolBattleDeaths(user, duration));
		if (getWoolBattleKillDeathRatio(user, duration) == 0) {
			return -1;
		}
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "killDeathRatio");
	}

	public static final long getWoolBattleWins(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "wins", "uuid",
				user.getUniqueId().toString());
		return o == null ? 0 : (long) o;
	}

	public static final long getWoolBattlePlacementWins(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "wins");
	}

	public static final long getWoolBattleLosses(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "losses", "uuid",
				user.getUniqueId().toString());
		return o == null ? 0 : (long) o;
	}

	public static final long getWoolBattlePlacementLosses(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "losses");
	}

	public static final long getWoolBattleDeaths(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "deaths", "uuid",
				user.getUniqueId().toString());
		return o == null ? 0 : (long) o;
	}

	public static final long getWoolBattlePlacementDeaths(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "deaths");
	}

	public static final long getWoolBattleKills(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "kills", "uuid",
				user.getUniqueId().toString());
		return o == null ? 0 : (long) o;
	}

	public static final long getWoolBattlePlacementKills(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "kills");
	}

	public static final double getWoolBattleElo(User user, Duration duration) {
		Object o = getObject(getTableName(WOOLBATTLE_BASE_NAME, duration), "elo", "uuid",
				user.getUniqueId().toString());
		return o == null ? 2000 : (double) o;
	}

	public static final long getWoolBattlePlacementElo(User user, Duration duration) {
		return getPlacement(user, getTableName(WOOLBATTLE_BASE_NAME, duration), "elo");
	}

	private static final long getPlacement(User user, String table, String column) {
		try {
			Result res = mysql.getResult(
					"SELECT COUNT(*) FROM {} "
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

	private static final void insert(String table, String key) {
		String[] columns = getColumnNames(table);
		if (columns.length != 0) {
			try {
				StringBuilder b = new StringBuilder();
				b.append("'" + key + "'");
				for (int i = 1; i < columns.length; i++) {
					b.append(", ").append("NULL");
				}
				b.append(')');
				PreparedStatement s = mysql.con.prepareStatement("INSERT INTO " + table + " VALUES (" + b);
				s.executeUpdate();
				s.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static final Object getObject(String table, String columnName, String primaryKey, String key) {
		try {
			Result res = mysql.getResult("SELECT {} FROM {} WHERE {}='{}'", columnName, table, primaryKey, key);
			if (!res.isEmpty()) {
				return res.getObject();
			}
			insert(table, key);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static final void setObject(String table, String columnName, String primaryKey, String key, Object object) {
		getObject(table, columnName, primaryKey, key);
		mysql.update("UPDATE {} SET {}='{}' WHERE {}='{}'", table, columnName, object.toString(), primaryKey, key);
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
						"jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=utf8", username,
						password);
				TABLES.forEach(t -> this.update(t));
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
					StatsPlugin.getInstance()
							.sendMessage(ChatColor.RED + "Could not connect to database. Shutting down server. "
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
				handleException(ex);
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
			handleException(e);
			return getResult(query, replace);
		}

		return res == null ? null : new MySQLResult(this, res);
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
			handleException(e);
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

	private static final String format(String msg, final String... replacements) {
		int i = 0;
		for (int j = 0; j < replacements.length; j++) {
			if (replacements[i].equals("true"))
				replacements[i] = "1";
			else if (replacements[i].equals("false"))
				replacements[i] = "0";
		}
		for (; msg.contains("{}") && replacements.length > i; i++)
//			msg = msg.replaceFirst("\\{\\}", replacements[i]);
			msg = Pattern.compile("\\{\\}").matcher(msg).replaceFirst(Matcher.quoteReplacement(replacements[i]));
		return msg;
	}

	private static final String[] getColumnNames(String table) {
		List<String> res = new ArrayList<>();
		try {
			ResultSet r = ((MySQLResult) mysql.getResult("SELECT * FROM " + table)).raw();
			for (int i = 0; i < r.getMetaData().getColumnCount(); i++)
				res.add(r.getMetaData().getColumnName(i + 1));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return res.toArray(new String[0]);
	}

	private final void handleException(Exception e) throws RuntimeException {
//		if (!(e instanceof CommunicationsException)) {
//			throw new RuntimeException(e);
//		}
		disconnect();
		connect();
		return;
	}

	private final String getRow(ResultSet res, String... keys) throws SQLException {
		StringBuilder builder = new StringBuilder().append("Entry[");
		for (int i = 0; i < keys.length; i++) {
			builder.append(res.getMetaData().getColumnName(i + 1)).append('=').append(res.getObject(keys[i]) + ", ");
		}
		return builder.substring(0, builder.length() - 2) + ']';
	}

	private final void appendRow(StringBuilder builder, ResultSet res, String... keys) throws SQLException {
		builder.append(getRow(res, keys));
	}

}
