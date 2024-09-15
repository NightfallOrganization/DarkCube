/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import eu.darkcube.system.bauserver.heads.Head;
import eu.darkcube.system.bauserver.heads.remote.Providers;
import org.mariadb.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseStorage {
    private static final String CONNECT_URL_FORMAT = "jdbc:mariadb://%s:%d/%s?serverTimezone=UTC";
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseStorage.class);
    private volatile HikariDataSource hikariDataSource;
    private ScheduledThreadPoolExecutor executor;
    private final DatabaseConfig config;

    public DatabaseStorage(DatabaseConfig config) {
        this.config = config;
    }

    public void load() throws HikariPool.PoolInitializationException {
        var hikariConfig = new HikariConfig();
        var endpoint = config.endpoint();

        executor = new ScheduledThreadPoolExecutor(1, r -> {
            var thread = new Thread(r);
            thread.setName("CustomThread");
            System.out.println("Create thread with runnable " + r);
            Thread.dumpStack();
            return thread;
        });
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

        hikariConfig.setJdbcUrl(CONNECT_URL_FORMAT.formatted(endpoint.address().host(), endpoint.address().port(), endpoint.database()));
        hikariConfig.setDriverClassName(Driver.class.getName());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        hikariConfig.setScheduledExecutor(executor);

        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(100);
        hikariConfig.setConnectionTimeout(10_000);
        hikariConfig.setValidationTimeout(10_000);

        this.hikariDataSource = new HikariDataSource(hikariConfig);

        create();

        if (size() == 0) {
            fetchFromProviders();
        }
    }

    public static void main() {
        var storage = new DatabaseStorage(new DatabaseConfig("rooadt", "", new ConnectionEndpoint("bauserver", new HostAndPort("localhost", 3306))));
        storage.load();
        if (storage.size() == 0) {
            for (var provider : Providers.providers()) {
                for (var category : provider.categories()) {
                    var heads = provider.heads(category);
                    storage.insert(heads);
                }

                for (var category : provider.categories()) {
                    System.out.println(category + ": " + storage.size(provider.name(), category));
                }
            }
        }

        System.out.println("Heads: " + storage.size());

        storage.close();
    }

    private static final String TABLE = "head_database";
    private static final String HEAD_READ_FORMAT = "`Name`,`Category`,`Provider`,`Texture`,`Tags`";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`Id` INT NOT NULL AUTO_INCREMENT, `Name` TEXT NOT NULL, `Category` VARCHAR(256) NOT NULL, `Provider` VARCHAR(256) NOT NULL, `Texture` VARCHAR(1024) NOT NULL, `Tags` TEXT, PRIMARY KEY (`Id`))";
    private static final String CLEAR = "TRUNCATE TABLE `" + TABLE + "`";
    private static final String INSERT = "INSERT INTO `" + TABLE + "` (`Id`, `Name`, `Category`, `Provider`, `Texture`, `Tags`) VALUES (NULL, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT " + HEAD_READ_FORMAT + " FROM `" + TABLE + "` ORDER BY `Id` LIMIT %d OFFSET %d";
    private static final String SELECT_BY_PROVIDER = "SELECT " + HEAD_READ_FORMAT + " FROM `" + TABLE + "` WHERE `Provider` = ? ORDER BY `Id` LIMIT %d OFFSET %d";
    private static final String SELECT_BY_PROVIDER_CATEGORY = "SELECT " + HEAD_READ_FORMAT + " FROM `" + TABLE + "` WHERE `Provider` = ? AND `CATEGORY` = ? ORDER BY `Id` LIMIT %d OFFSET %d";
    private static final String SIZE = "SELECT count(*) FROM `" + TABLE + "`";
    private static final String SIZE_BY_PROVIDER = "SELECT count(*) FROM `" + TABLE + "` WHERE `Provider` = ?";
    private static final String SIZE_BY_PROVIDER_CATEGORY = "SELECT count(*) FROM `" + TABLE + "` WHERE `Provider` = ? AND `Category` = ?";

    public void create() {
        try (var connection = connection()) {
            try (var statement = connection.createStatement()) {
                statement.execute(CREATE_TABLE);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to create head database", e);
        }
    }

    public void fetchFromProviders() {
        for (var provider : Providers.providers()) {
            for (var category : provider.categories()) {
                var heads = provider.heads(category);
                insert(heads);
            }
        }
    }

    public int size(String provider) {
        try (var connection = connection()) {
            try (var statement = connection.prepareStatement(SIZE_BY_PROVIDER)) {
                statement.setString(1, provider);
                var rs = statement.executeQuery();
                return size(rs);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get head database size", e);
            return 0;
        }
    }

    public int size(String provider, String category) {
        try (var connection = connection()) {
            try (var statement = connection.prepareStatement(SIZE_BY_PROVIDER_CATEGORY)) {
                statement.setString(1, provider);
                statement.setString(2, category);
                var rs = statement.executeQuery();
                return size(rs);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get head database size", e);
            return 0;
        }
    }

    public int size() {
        try (var connection = connection()) {
            try (var statement = connection.createStatement()) {
                var rs = statement.executeQuery(SIZE);
                return size(rs);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to get head database size", e);
            return 0;
        }
    }

    private int size(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            LOGGER.error("Failed to get head database size: ResultSet was empty?");
            return 0;
        }
        return rs.getInt(1);
    }

    public List<Head> select(String provider, int index, int count) {
        try (var connection = connection()) {
            try (var statement = connection.prepareStatement(SELECT_BY_PROVIDER.formatted(count, index))) {
                statement.setString(1, provider);
                var rs = statement.executeQuery();
                return read(rs);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to select {} elements from head database at index {} with provider {}", count, index, provider, e);
            return List.of();
        }
    }

    public List<Head> select(String provider, String category, int index, int count) {
        try (var connection = connection()) {
            try (var statement = connection.prepareStatement(SELECT_BY_PROVIDER_CATEGORY.formatted(count, index))) {
                statement.setString(1, provider);
                statement.setString(2, category);
                var rs = statement.executeQuery();
                return read(rs);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to select {} elements from head database at index {} with provider {} and category {}", count, index, provider, category, e);
            return List.of();
        }
    }

    public List<Head> select(int index, int count) {
        try (var connection = connection()) {
            try (var statement = connection.createStatement()) {
                var rs = statement.executeQuery(SELECT_ALL.formatted(count, index));
                return read(rs);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to select {} elements from head database at index {}", count, index, e);
            return List.of();
        }
    }

    private List<Head> read(ResultSet rs) throws SQLException {
        var heads = new ArrayList<Head>();
        while (rs.next()) {
            var name = rs.getString("Name");
            var category = rs.getString("Category");
            var provider = rs.getString("Provider");
            var texture = rs.getString("Texture");
            var tags = tags("Tags", rs);
            var head = new Head(name, texture, category, provider, tags);
            heads.add(head);
        }
        return List.copyOf(heads);
    }

    public void insert(List<Head> heads) {
        LOGGER.info("Adding {} to head database", heads.size());
        if (heads.isEmpty()) {
            LOGGER.info("Skipping insert with head count of 0");
            return;
        }
        try (var connection = connection()) {
            var batchSize = 1024;
            var index = 0;
            try (var statement = connection.prepareStatement(INSERT)) {
                for (var head : heads) {
                    statement.setString(1, head.name());
                    statement.setString(2, head.category());
                    statement.setString(3, head.provider());
                    statement.setString(4, head.texture());
                    statement.setString(5, head.tags().isEmpty() ? "NULL" : String.join(",", head.tags()));
                    statement.addBatch();
                    if (++index % batchSize == 0) {
                        statement.executeBatch();
                    }
                }
                statement.executeBatch();
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to insert heads", e);
        }
    }

    private static List<String> tags(String name, ResultSet resultSet) throws SQLException {
        var tagsString = resultSet.getString(name);
        if (resultSet.wasNull()) return List.of();
        return List.of(tagsString.split(","));
    }

    public void clear() {
        LOGGER.info("Clearing head database");
        try (var connection = connection()) {
            try (var statement = connection.createStatement()) {
                statement.execute(CLEAR);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to clear head database", e);
        }
    }

    public Connection connection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        this.hikariDataSource.close();
        this.hikariDataSource = null;
        executor.shutdownNow();
        var e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            var driver = e.nextElement();
            if (driver.getClass() == Driver.class) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            }
        }
        LOGGER.error("Failed to unregister driver");
    }
}
