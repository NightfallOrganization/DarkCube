/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.statsreset.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.bukkit.Bukkit;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import eu.darkcube.system.stats.api.Duration;

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

	private static final String getTableName(String basename, Duration duration) {
		return basename + ("_" + duration.format()).replace("_" + Duration.ALLTIME.format(), "");
	}

	public static final void clearTables(Duration duration) {
		for (String basename : eu.darkcube.system.stats.api.mysql.MySQL.BASE_NAMES) {
			clearTable(basename, duration);
		}
	}

	public static final void clearTable(String basename, Duration duration) {
		mysql.update("TRUNCATE " + getTableName(basename, duration));
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
								+ "?characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true",
						username, password);
				TABLES.forEach(t -> this.update(t));
//				StatsPlugin.getInstance().sendConsole("[MySQL] Connected!");

			} catch (SQLException ex) {
				ex.printStackTrace();
				if (ex instanceof CommunicationsException) {
//					StatsPlugin.getInstance().sendConsole(ChatColor.RED
//							+ "Could not connect to database. Shutting down server. "
//							+ "\nPlease ensure your IP in the config is correct and the MySQL database is online!");
					loadError();
				} else if (ex instanceof MySQLSyntaxErrorException) {
//					StatsPlugin.getInstance().sendConsole(ChatColor.RED
//							+ "Could not connect to database. Shutting down server. "
//							+ "\nPlease ensure your values in the config are correct, such like password, username.\n"
//							+ ex.getMessage());
					loadError();
				} else if (ex.getMessage().startsWith("Access denied for user")) {
//					StatsPlugin.getInstance()
//							.sendMessage(ChatColor.RED + "Could not connect to database. Shutting down server. "
//									+ "\nThe entered password is wrong!\n" + ex.getMessage());
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
//				StatsPlugin.getInstance().sendConsole("[MySQL] Disconnected!");
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
//			StatsPlugin.getInstance().sendMessage(ChatColor.DARK_RED + "Stopping server!");
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
//		Bukkit.shutdown();
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

//	private static final String[] getColumnNames(String table) {
//		List<String> res = new ArrayList<>();
//		try {
//			ResultSet r = ((MySQLResult) mysql.getResult("SELECT * FROM " + table)).raw();
//			for (int i = 0; i < r.getMetaData().getColumnCount(); i++)
//				res.add(r.getMetaData().getColumnName(i + 1));
//		} catch (SQLException ex) {
//			ex.printStackTrace();
//		}
//		return res.toArray(new String[0]);
//	}

	private final void handleException(Exception e) throws RuntimeException {
		if (!(e instanceof CommunicationsException)) {
			throw new RuntimeException(e);
		}
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
