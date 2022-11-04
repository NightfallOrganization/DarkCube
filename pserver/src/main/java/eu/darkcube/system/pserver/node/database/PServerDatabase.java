package eu.darkcube.system.pserver.node.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.database.AbstractDatabaseProvider;
import de.dytanic.cloudnet.ext.database.mysql.MySQLDatabaseProvider;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.UniqueId;

public class PServerDatabase extends Database {

	private static final String[] COMMANDS;

	public PServerDatabase() {
		reload();
	}

	private void reload() {
		executeStatement(COMMANDS[0]);
	}

	private Connection getConnection() {
		try {
			MySQLDatabaseProvider provider = ((MySQLDatabaseProvider) CloudNet.getInstance()
					.getServicesRegistry()
					.getService(AbstractDatabaseProvider.class, "mysql"));
			try {
				return provider.getConnection();
			} catch (Exception ex) {
			}
			provider.init();
			return provider.getConnection();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean update(PServer pserver) {
		boolean success = true;
		UniqueId id = pserver.getId();
		for (UUID owner : pserver.getOwners()) {
			success &= update(id, owner);
		}
		return success;
	}

	public boolean update(UniqueId pserver, UUID owner) {
		if (!contains(owner, pserver)) {
			return insert(owner, pserver);
		}
		return true;
	}

	public boolean contains(UniqueId pserver) {
		String cmd = String.format(COMMANDS[5], pserver.toString());
		return executeQuery(cmd, r -> r.first());
	}

	public boolean contains(UUID owner, UniqueId pserver) {
		String cmd = String.format(COMMANDS[2], owner.toString(), pserver.toString());
		return executeQuery(cmd, r -> r.first());
	}

	private boolean insert(UUID owner, UniqueId pserver) {
		String cmd = String.format(COMMANDS[1], owner.toString(), pserver.toString());
		return executeStatement(cmd);
	}

	public boolean delete(UniqueId pserver) {
		String cmd = String.format(COMMANDS[3], pserver.toString());
		return executeStatement(cmd);
	}

	public boolean delete(UniqueId pserver, UUID owner) {
		String cmd = String.format(COMMANDS[4], pserver.toString(), owner.toString());
		return executeStatement(cmd);
	}

	public Collection<UniqueId> getPServers(UUID owner) {
		return executeQuery(String.format(COMMANDS[7], owner.toString()), r -> {
			Collection<UniqueId> col = new ArrayList<>();
			while (r.next()) {
				col.add(new UniqueId(r.getString(1)));
			}
			return col;
		});
	}

	public Collection<UniqueId> getUsedPServerIDs() {
		String cmd = String.format(COMMANDS[6]);
		return executeQuery(cmd, r -> {
			Collection<UniqueId> col = new ArrayList<>();
			while (r.next()) {
				col.add(new UniqueId(r.getString(1)));
			}
			return col;
		});
	}

	public Collection<UUID> getOwners(UniqueId pserver) {
		return executeQuery(String.format(COMMANDS[8], pserver.toString()), r -> {
			Collection<UUID> col = new ArrayList<>();
			while (r.next()) {
				col.add(UUID.fromString(r.getString(1)));
			}
			return col;
		});
	}

	private <T> T executeQuery(String query, ExceptionalFunction<ResultSet, T> func) {
		try {
			Connection con = getConnection();
			PreparedStatement prep = con.prepareStatement(query);
			ResultSet r = prep.executeQuery();
			T t = func.apply(r);
			prep.close();
			r.close();
			con.close();
			return t;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private boolean executeStatement(String statement) {
		try {
			Connection con = getConnection();
			PreparedStatement prep = con.prepareStatement(statement);
			boolean suc = prep.execute();
			prep.close();
			con.close();
			return suc;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	static {
		COMMANDS = new String[9];
		//@formatter:off
		COMMANDS[0] = 
					"CREATE TABLE IF NOT EXISTS pserver (" + 
					"`ID` INT NOT NULL AUTO_INCREMENT," +
					"`PLAYER` VARCHAR(36) NOT NULL," + 
					"`SERVER` TEXT NOT NULL," + 
					"PRIMARY KEY (`ID`)" + ")";
		COMMANDS[1] = "INSERT INTO `pserver`(`ID`, `PLAYER`, `SERVER`) VALUES (0,'%s','%s')";
		COMMANDS[2] = "SELECT * FROM `pserver` WHERE `PLAYER`='%s' AND `SERVER`='%s'";
		COMMANDS[3] = "DELETE FROM `pserver` WHERE `SERVER`='%s'";
		COMMANDS[4] = "DELETE FROM `pserver` WHERE `SERVER`='%s' AND `PLAYER`='%s'";
		COMMANDS[5] = "SELECT * FROM `pserver` WHERE `SERVER`='%s'";
		COMMANDS[6] = "SELECT DISTINCT `SERVER` FROM `pserver` WHERE 1";
		COMMANDS[7] = "SELECT `SERVER` FROM `pserver` WHERE `PLAYER`='%s'";
		COMMANDS[8] = "SELECT `PLAYER` FROM `pserver` WHERE `SERVER`='%s'";
		//@formatter:on
	}

	private static interface ExceptionalFunction<T, Q> {
		Q apply(T t) throws Exception;
	}
}
