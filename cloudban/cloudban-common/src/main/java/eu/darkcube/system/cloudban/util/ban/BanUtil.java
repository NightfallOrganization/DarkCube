/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.ban;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import de.dytanic.cloudnet.driver.database.DatabaseProvider;
import eu.darkcube.system.cloudban.util.gson.BanSerializer;
import eu.darkcube.system.cloudban.util.gson.DateTimeSerializer;
import eu.darkcube.system.cloudban.util.gson.DurationSerializer;
import eu.darkcube.system.cloudban.util.gson.ServerSerializer;
import eu.darkcube.system.cloudban.util.gson.StringBuilderExclusion;

public class BanUtil {

	public static final Gson GSON = new GsonBuilder().setExclusionStrategies(new StringBuilderExclusion()).registerTypeAdapter(Server.class, new ServerSerializer()).registerTypeAdapter(Ban.class, new BanSerializer()).registerTypeAdapter(DateTime.class, new DateTimeSerializer()).registerTypeAdapter(Duration.class, new DurationSerializer()).create();

	static {
		init();
	}

	private static void init() {
		dbProvider = CloudNetDriver.getInstance().getDatabaseProvider();
		dbUserHistory = dbProvider.getDatabase(DB_USER_HISTORY);
		dbUserInformation = dbProvider.getDatabase(DB_USER_INFORMATION);
		dbReasons = dbProvider.getDatabase(DB_REASONS);
		dbServers = dbProvider.getDatabase(DB_SERVERS);
	}

	private static final String DB_USER_HISTORY = "cloudban_userhistory";
	private static final String DB_USER_INFORMATION = "cloudban_userinformation";
	private static final String DB_REASONS = "cloudban_reasons";
	private static final String DB_SERVERS = "cloudban_servers";

	private static DatabaseProvider dbProvider;
	private static Database dbUserHistory;
	private static Database dbUserInformation;
	private static Database dbReasons;
	private static Database dbServers;

	public static boolean addBanToUserInformation(UUID uuid, Ban ban) {
		BanInformation i = getUserInformation(uuid);
		boolean suc = i.getBans().add(ban);
		setUserInformation(uuid, i);
		return suc;
	}

	public static boolean removeBanFromUserInformation(UUID uuid, Ban ban) {
		BanInformation i = getUserInformation(uuid);
		boolean suc = i.getBans().remove(ban);
		setUserInformation(uuid, i);
		return suc;
	}

	public static boolean addBanToUserHistory(UUID uuid, Ban ban) {
		BanHistory i = getUserHistory(uuid);
		boolean suc = i.getBans().add(ban);
		setUserHistory(uuid, i);
		return suc;
	}

	public static boolean removeBanFromUserHistory(UUID uuid, Ban ban) {
		BanHistory i = getUserHistory(uuid);
		boolean suc = i.getBans().remove(ban);
		setUserHistory(uuid, i);
		return suc;
	}

	public static void clearUserInformation(UUID uuid) {
		dbUserInformation.delete(uuid.toString());
	}

	public static BanInformation getUserInformation(UUID uuid) {
		if (!dbUserInformation.contains(uuid.toString()))
			return new BanInformation();
		return BanInformation.fromMySQLDocument(dbUserInformation.get(uuid.toString()));
	}

	public static void setUserInformation(UUID uuid, BanInformation history) {
		if (history.isEmpty()) {
			clearUserInformation(uuid);
		} else if (!dbUserInformation.contains(uuid.toString())) {
			dbUserInformation.insert(uuid.toString(), history.toMySQLDocument());
		} else {
			dbUserInformation.update(uuid.toString(), history.toMySQLDocument());
		}
	}

	public static void clearUserHistory(UUID uuid) {
		dbUserHistory.delete(uuid.toString());
	}

	public static BanHistory getUserHistory(UUID uuid) {
		if (!dbUserHistory.contains(uuid.toString()))
			return new BanHistory();
		return BanHistory.fromMySQLDocument(dbUserHistory.get(uuid.toString()));
	}

	public static void setUserHistory(UUID uuid, BanHistory history) {
		if (history.isEmpty()) {
			clearUserHistory(uuid);
		} else if (!dbUserHistory.contains(uuid.toString())) {
			dbUserHistory.insert(uuid.toString(), history.toMySQLDocument());
		} else {
			dbUserHistory.update(uuid.toString(), history.toMySQLDocument());
		}
	}

	public static Collection<Server> getServers() {
		Collection<Server> s = dbServers.entries().keySet().stream().map(Server::new).collect(Collectors.toSet());
		s.add(Server.GLOBAL);
		return s;
	}

	public static void addServer(Server server) {
		dbServers.insert(server.getServer(), new JsonDocument());
	}

	public static void removeServer(Server server) {
		dbServers.delete(server.getServer());
	}

	public static void clearServers() {
		dbServers.clear();
	}

	public static void removeReason(Reason reason) {
		dbReasons.delete(reason.getKey());
	}

	public static void addReason(Reason reason) {
		removeReason(reason);
		dbReasons.insert(reason.getKey(), reason.toMySQLDocument());
	}

	public static void setReasons(Collection<Reason> reasons) {
		clearReasons();
		for (Reason reason : reasons) {
			addReason(reason);
		}
	}

	public static void clearReasons() {
		dbReasons.clear();
	}

	public static Collection<Reason> getReasons() {
		return dbReasons.entries().keySet().stream().map(BanUtil::getReasonByKey).collect(Collectors.toSet());
	}

	public static Reason getReasonByKey(String key) {
		System.out.println("query reason by " + key);
		return Reason.fromMySQLDocument(key, dbReasons.get(key));
	}
}
