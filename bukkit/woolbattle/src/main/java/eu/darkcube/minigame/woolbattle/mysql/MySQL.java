package eu.darkcube.minigame.woolbattle.mysql;

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

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.DefaultPlayerPerks;
import eu.darkcube.minigame.woolbattle.perk.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.DefaultUserData;
import eu.darkcube.minigame.woolbattle.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.user.UserData;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;

public class MySQL {

	private static final Collection<String> TABLES = new ArrayList<>();

	private static MySQL mysql;
	private static int emptyCount = 0;

	private Connection con;
	private final String host;
	private final String port;
	private final String database;
	private final String username;
	private final String password;

	static {
		TABLES.add("CREATE TABLE IF NOT EXISTS woolbattle_userdata (uuid VARCHAR("
						+ UUID.randomUUID().toString().length()
						+ "), userdata TEXT, PRIMARY KEY(uuid))");
	}

	public static final UserData loadUserData(UUID uuid) {
		Object o = getObject("woolbattle_userdata", "userdata", "uuid", uuid.toString());
		DefaultUserData data = GsonSerializer.gson.fromJson(o == null
						? new DefaultUserData().toString()
						: o.toString(), DefaultUserData.class);
		if (data.getHeightDisplay() == null) {
			data.setHeightDisplay(HeightDisplay.getDefault());
		}
		if (data.getWoolSubtractDirection() == null) {
			data.setWoolSubtractDirection(WoolSubtractDirection.getDefault());
		}
		PlayerPerks perks = data.getPerks();
		if (perks.getActivePerk1().toType() == null) {
			perks.setActivePerk1(new DefaultPlayerPerks().getActivePerk1());
		}
		if (perks.getActivePerk2().toType() == null) {
			perks.setActivePerk2(new DefaultPlayerPerks().getActivePerk2());
		}
		if (perks.getPassivePerk().toType() == null) {
			perks.setPassivePerk(new DefaultPlayerPerks().getPassivePerk());
		}
		saveUserData(uuid, data);
		return data;
	}

	public static final void saveUserData(User user) {
		saveUserData(user.getUniqueId(), user.getData());
	}

	public static final void saveUserData(UUID uuid, UserData data) {
		setObject("woolbattle_userdata", "userdata", "uuid", uuid.toString(), data.toString());
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
				PreparedStatement s = mysql.con.prepareStatement("INSERT INTO "
								+ table + " VALUES (" + b);
				s.executeUpdate();
				s.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static final Object getObject(String table, String columnName,
					String primaryKey, String key) {
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

	private static final void setObject(String table, String columnName,
					String primaryKey, String key, Object object) {
		getObject(table, columnName, primaryKey, key);
		mysql.update("UPDATE {} SET {}='{}' WHERE {}='{}'", table, columnName, object.toString(), primaryKey, key);
	}

	public MySQL() {
		if (mysql != null)
			throw new IllegalAccessError("MySQL service is already running!");
		mysql = this;
		WoolBattle.getInstance().reloadConfig("mysql");
		host = WoolBattle.getInstance().getConfig("mysql").getString("host");
		port = WoolBattle.getInstance().getConfig("mysql").getString("port");
		database = WoolBattle.getInstance().getConfig("mysql").getString("database");
		username = WoolBattle.getInstance().getConfig("mysql").getString("username");
		password = WoolBattle.getInstance().getConfig("mysql").getString("password");
	}

	public final void connect() {
		if (!isConnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":"
								+ port + "/" + database
								+ "?characterEncoding=utf8", username, password);
				TABLES.forEach(t -> this.update(t));
				WoolBattle.getInstance().sendConsole("[MySQL] Connected!");
			} catch (SQLException ex) {
				if (ex instanceof CommunicationsException) {
					WoolBattle.getInstance().sendConsole(ChatColor.RED
									+ "Could not connect to database. Shutting down server. "
									+ "\nPlease ensure your IP in the config is correct and the MySQL database is online!");
					loadError();
				} else if (ex instanceof MySQLSyntaxErrorException) {
					WoolBattle.getInstance().sendConsole(ChatColor.RED
									+ "Could not connect to database. Shutting down server. "
									+ "\nPlease ensure your values in the config are correct, such like password, username.\n"
									+ ex.getMessage());
					loadError();
				} else if (ex.getMessage().startsWith("Access denied for user")) {
					WoolBattle.getInstance().sendMessage(ChatColor.RED
									+ "Could not connect to database. Shutting down server. "
									+ "\nThe entered password is wrong!\n"
									+ ex.getMessage());
					loadError();
				} else {
					handleException(ex);
				}
			}
		}
	}

	public final void disconnect() {
		if (isConnected()) {
			try {
				con.close();
				WoolBattle.getInstance().sendConsole("[MySQL] Disconnected!");
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

	public final Result getResult(final String query,
					final CharSequence... replace) {
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
			WoolBattle.getInstance().sendMessage(ChatColor.DARK_RED
							+ "Stopping server!");
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

	private static final String format(String msg,
					final String... replacements) {
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
			ResultSet r = ((MySQLResult) mysql.getResult("SELECT * FROM "
							+ table)).raw();
			for (int i = 0; i < r.getMetaData().getColumnCount(); i++)
				res.add(r.getMetaData().getColumnName(i + 1));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return res.toArray(new String[0]);
	}

	private final void handleException(Exception e) throws RuntimeException {
		if (!(e instanceof CommunicationsException)) {
			throw new RuntimeException(e);
		}
		disconnect();
		connect();
		return;
	}

	private final String getRow(ResultSet res, String... keys)
					throws SQLException {
		StringBuilder builder = new StringBuilder().append("Entry[");
		for (int i = 0; i < keys.length; i++) {
			builder.append(res.getMetaData().getColumnName(i
							+ 1)).append('=').append(res.getObject(keys[i])
											+ ", ");
		}
		return builder.substring(0, builder.length() - 2) + ']';
	}

	private final void appendRow(StringBuilder builder, ResultSet res,
					String... keys) throws SQLException {
		builder.append(getRow(res, keys));
	}
}