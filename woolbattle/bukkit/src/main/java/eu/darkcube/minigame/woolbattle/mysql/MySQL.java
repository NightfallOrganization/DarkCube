/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.mysql;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.old.DefaultUserData;
import eu.darkcube.minigame.woolbattle.old.UserData;
import eu.darkcube.minigame.woolbattle.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;
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

	private static MySQL mysql;
	private static int emptyCount = 0;

	static {
		TABLES.add(
				"CREATE TABLE IF NOT EXISTS woolbattle_userdata (uuid VARCHAR(" + UUID.randomUUID()
						.toString().length() + "), userdata TEXT, PRIMARY KEY(uuid))");
	}

	private final String host;
	private final String port;
	private final String database;
	private final String username;
	private final String password;
	private Connection con;

	public MySQL() {
		if (mysql != null)
			throw new IllegalAccessError("MySQL service is already running!");
		mysql = this;
		WoolBattleBukkit.instance().reloadConfig("mysql");
		host = WoolBattleBukkit.instance().getConfig("mysql").getString("host");
		port = WoolBattleBukkit.instance().getConfig("mysql").getString("port");
		database = WoolBattleBukkit.instance().getConfig("mysql").getString("database");
		username = WoolBattleBukkit.instance().getConfig("mysql").getString("username");
		password = WoolBattleBukkit.instance().getConfig("mysql").getString("password");
	}

	//	public static void saveUserData(WBUser user) {
	//		saveUserData(user.getUniqueId(), user.getData());
	//	}

	//	public static void saveUserData(UUID uuid, UserData data) {
	//		setObject(uuid.toString(), data.toString());
	//	}

	public static UserData loadUserData(UUID uuid) {
		Object o = getObject(uuid.toString());
		if (o == null)
			return null;
		DefaultUserData data = GsonSerializer.gson.fromJson(o.toString(), DefaultUserData.class);
		if (data.getHeightDisplay() == null) {
			data.setHeightDisplay(HeightDisplay.getDefault());
		}
		if (data.getWoolSubtractDirection() == null) {
			data.setWoolSubtractDirection(WoolSubtractDirection.getDefault());
		}
		//		saveUserData(uuid, data);
		return data;
	}

	private static void insert(String key) {
		String[] columns = getColumnNames();
		if (columns.length != 0) {
			try {
				StringBuilder b = new StringBuilder();
				b.append("'").append(key).append("'");
				for (int i = 1; i < columns.length; i++) {
					b.append(", ").append("NULL");
				}
				b.append(')');
				PreparedStatement s = mysql.con.prepareStatement(
						"INSERT INTO " + "woolbattle_userdata" + " VALUES (" + b);
				s.executeUpdate();
				s.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static Object getObject(String key) {
		try {
			Result res = mysql.getResult("SELECT {} FROM {} WHERE {}='{}'", "userdata",
					"woolbattle_userdata", "uuid", key);
			if (!res.isEmpty()) {
				return res.getObject();
			}
			insert(key);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static void setObject(String key, Object object) {
		getObject(key);
		mysql.update("UPDATE {} SET {}='{}' WHERE {}='{}'", "woolbattle_userdata", "userdata",
				object.toString(), "uuid", key);
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

	private static String[] getColumnNames() {
		List<String> res = new ArrayList<>();
		try {
			ResultSet r = mysql.getResult("SELECT * FROM " + "woolbattle_userdata").raw();
			for (int i = 0; i < r.getMetaData().getColumnCount(); i++)
				res.add(r.getMetaData().getColumnName(i + 1));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return res.toArray(new String[0]);
	}

	public final void connect() {
		if (!isConnected()) {
			try {
				con = DriverManager.getConnection(
						"jdbc:mysql://" + host + ":" + port + "/" + database
								+ "?characterEncoding=utf8", username, password);
				TABLES.forEach(this::update);
				WoolBattleBukkit.instance().sendConsole("[MySQL] Connected!");
			} catch (SQLException ex) {
				if (ex instanceof CommunicationsException) {
					WoolBattleBukkit.instance().sendConsole(
							ChatColor.RED + "Could not connect to database. Shutting down server. "
									+ "\nPlease ensure your IP in the config is correct and the MySQL database is online!");
					loadError();
				} else if (ex instanceof MySQLSyntaxErrorException) {
					WoolBattleBukkit.instance().sendConsole(
							ChatColor.RED + "Could not connect to database. Shutting down server. "
									+ "\nPlease ensure your values in the config are correct, such like password, username.\n"
									+ ex.getMessage());
					loadError();
				} else if (ex.getMessage().startsWith("Access denied for user")) {
					WoolBattleBukkit.instance().sendMessage(
							ChatColor.RED + "Could not connect to database. Shutting down server. "
									+ "\nThe entered password is wrong!\n" + ex.getMessage());
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
				WoolBattleBukkit.instance().sendConsole("[MySQL] Disconnected!");
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

		ResultSet res;
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
			handleException(e);
		}
	}

	public void loadError() {
		try {
			WoolBattleBukkit.instance().sendMessage(ChatColor.DARK_RED + "Stopping server!");
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		Bukkit.shutdown();
	}

	public final String toString(ResultSet res) throws SQLException {
		int startIndex = res.getRow();
		StringBuilder builder = new StringBuilder();
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

	private void handleException(Exception e) throws RuntimeException {
		if (!(e instanceof CommunicationsException)) {
			throw new RuntimeException(e);
		}
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
