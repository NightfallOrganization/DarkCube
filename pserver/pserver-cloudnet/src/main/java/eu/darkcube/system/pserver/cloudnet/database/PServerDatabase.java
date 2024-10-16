/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.node.database.NodeDatabaseProvider;
import eu.cloudnetservice.node.database.sql.SQLDatabaseProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.pserver.cloudnet.PServerModule;
import eu.darkcube.system.pserver.common.UniqueId;

public class PServerDatabase extends Database {

    private static final String[] COMMANDS;

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

    public PServerDatabase() {
        reload();
    }

    private void reload() {
        executeStatement(COMMANDS[0]);
    }

    private Connection getConnection() {
        try {

            ServiceRegistry registry = InjectionLayer.boot().instance(ServiceRegistry.class);
            SQLDatabaseProvider provider = ((SQLDatabaseProvider) registry.provider(NodeDatabaseProvider.class, PServerModule.getInstance().sqlDatabase));
            try {
                return provider.connection();
            } catch (Exception ignored) {
            }
            provider.init();
            return provider.connection();
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public boolean contains(UniqueId pserver) {
        String cmd = String.format(COMMANDS[5], pserver.toString());
        return Boolean.TRUE.equals(executeQuery(cmd, ResultSet::next));
    }

    public boolean contains(UUID owner, UniqueId pserver) {
        String cmd = String.format(COMMANDS[2], owner.toString(), pserver.toString());
        return Boolean.TRUE.equals(executeQuery(cmd, ResultSet::next));
    }

    private boolean insert(UUID owner, UniqueId pserver) {
        String cmd = String.format(COMMANDS[1], owner.toString(), pserver.toString());
        return executeStatement(cmd);
    }

    public boolean update(UniqueId pserver, UUID owner) {
        if (!contains(owner, pserver)) {
            return insert(owner, pserver);
        }
        return true;
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

    private <T> @Nullable T executeQuery(String query, ExceptionalFunction<ResultSet, T> func) {
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

    private interface ExceptionalFunction<T, Q> {
        Q apply(T t) throws Exception;
    }
}
