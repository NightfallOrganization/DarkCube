/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dytanic.cloudnet.ext.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariDataSource;
import de.dytanic.cloudnet.common.concurrent.IThrowableCallback;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.database.sql.SQLDatabaseProvider;
import de.dytanic.cloudnet.driver.database.Database;
import de.dytanic.cloudnet.ext.database.mysql.util.MySQLConnectionEndpoint;

public final class MySQLDatabaseProvider extends SQLDatabaseProvider {

	// private static final long NEW_CREATION_DELAY = 600000;
	//
	protected static final String[] TABLE_TYPE = new String[] {"TABLE"};
	// protected final HikariDataSource hikariDataSource = new HikariDataSource();
	//
	// private final JsonDocument config;
	//
	// private List<MySQLConnectionEndpoint> addresses;
	//
	// public MySQLDatabaseProvider(JsonDocument config, ExecutorService executorService) {
	// super(executorService);
	// this.config = config;
	// }
	//
	// @Override
	// public boolean init() {
	// this.addresses = this.config.get("addresses", CloudNetMySQLDatabaseModule.TYPE);
	// MySQLConnectionEndpoint endpoint =
	// this.addresses.get(new Random().nextInt(this.addresses.size()));
	//
	// this.hikariDataSource.setJdbcUrl("jdbc:mysql://" + endpoint.address().getHost() + ":"
	// + endpoint.getAddress().getPort() + "/" + endpoint.database()
	// + String.format("?useSSL=%b&trustServerCertificate=%b", endpoint.useSsl(),
	// endpoint.useSsl()));
	//
	// // base configuration
	// this.hikariDataSource.setUsername(this.config.getString("username"));
	// this.hikariDataSource.setPassword(this.config.getString("password"));
	// this.hikariDataSource.setDriverClassName("com.mysql.jdbc.Driver");
	//
	// int maxPoolSize = this.config.getInt("connectionMaxPoolSize");
	//
	// this.hikariDataSource.setMaximumPoolSize(maxPoolSize);
	// this.hikariDataSource
	// .setMinimumIdle(Math.min(maxPoolSize, this.config.getInt("connectionMinPoolSize")));
	// this.hikariDataSource.setConnectionTimeout(this.config.getInt("connectionTimeout"));
	// this.hikariDataSource.setValidationTimeout(this.config.getInt("validationTimeout"));
	//
	// this.hikariDataSource.validate();
	// return true;
	// }
	//
	// @Override
	// public Database getDatabase(String name) {
	// Preconditions.checkNotNull(name);
	//
	// this.removedOutdatedEntries();
	//
	// if (!this.cachedDatabaseInstances.contains(name)) {
	// this.cachedDatabaseInstances.add(name, System.currentTimeMillis() + NEW_CREATION_DELAY,
	// new MySQLDatabase(this, name, super.executorService));
	// }
	//
	// return this.cachedDatabaseInstances.getSecond(name);
	// }
	//
	// @Override
	// public boolean deleteDatabase(String name) {
	// Preconditions.checkNotNull(name);
	//
	// this.cachedDatabaseInstances.remove(name);
	//
	// if (this.containsDatabase(name)) {
	// try (Connection connection = this.getConnection();
	// PreparedStatement preparedStatement =
	// connection.prepareStatement("DROP TABLE " + name)) {
	// return preparedStatement.executeUpdate() != -1;
	// } catch (SQLException exception) {
	// exception.printStackTrace();
	// }
	// }
	//
	// return false;
	// }
	//
	// @Override
	// public Collection<String> getDatabaseNames() {
	// try (Connection con = this.hikariDataSource.getConnection()) {
	// ResultSet meta = con.getMetaData().getTables(null, null, null, TABLE_TYPE);
	// Collection<String> names = new ArrayList<>();
	// while (meta.next()) {
	// names.add(meta.getString("table_name"));
	// }
	// return names;
	// } catch (SQLException ex) {
	// ex.printStackTrace();
	// return new HashSet<>();
	// }
	// // return this.executeQuery(
	// // "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='PUBLIC'",
	// // resultSet -> {
	// // Collection<String> collection = new ArrayList<>();
	// // while (resultSet.next()) {
	// // collection.add(resultSet.getString("table_name"));
	// // }
	// //
	// // return collection;
	// // });
	// }
	//
	// @Override
	// public String getName() {
	// return this.config.getString("database");
	// }
	//
	// @Override
	// public void close() throws Exception {
	// super.close();
	//
	// this.hikariDataSource.close();
	// }
	//
	// @Override
	// public Connection getConnection() throws SQLException {
	// return this.hikariDataSource.getConnection();
	// }
	//
	// public HikariDataSource getHikariDataSource() {
	// return this.hikariDataSource;
	// }
	//
	// public JsonDocument getConfig() {
	// return this.config;
	// }
	//
	// public List<MySQLConnectionEndpoint> getAddresses() {
	// return this.addresses;
	// }
	//
	// @Override
	// public int executeUpdate(String query, Object... objects) {
	// Preconditions.checkNotNull(query);
	// Preconditions.checkNotNull(objects);
	//
	// try (Connection connection = this.getConnection();
	// PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	// int i = 1;
	// for (Object object : objects) {
	// preparedStatement.setString(i++, object.toString());
	// }
	//
	// return preparedStatement.executeUpdate();
	//
	// } catch (SQLException exception) {
	// exception.printStackTrace();
	// }
	//
	// return -1;
	// }
	//
	// @Override
	// public <T> T executeQuery(String query, IThrowableCallback<ResultSet, T> callback,
	// Object... objects) {
	// Preconditions.checkNotNull(query);
	// Preconditions.checkNotNull(callback);
	// Preconditions.checkNotNull(objects);
	//
	// try (Connection connection = this.getConnection();
	// PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	// int i = 1;
	// for (Object object : objects) {
	// preparedStatement.setString(i++, object.toString());
	// }
	//
	// try (ResultSet resultSet = preparedStatement.executeQuery()) {
	// return callback.call(resultSet);
	// }
	//
	// } catch (Throwable e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }
	private static final long NEW_CREATION_DELAY = 600000L;

	protected final HikariDataSource hikariDataSource = new HikariDataSource();

	private final JsonDocument config;

	private List<MySQLConnectionEndpoint> addresses;

	public MySQLDatabaseProvider(JsonDocument config, ExecutorService executorService) {
		super(executorService);
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init() {
		this.addresses = (List<MySQLConnectionEndpoint>) this.config.get("addresses",
				CloudNetMySQLDatabaseModule.TYPE);
		MySQLConnectionEndpoint endpoint =
				this.addresses.get((new Random()).nextInt(this.addresses.size()));
		this.hikariDataSource.setJdbcUrl("jdbc:mysql://" + endpoint.getAddress().getHost() + ":"
				+ endpoint.getAddress().getPort() + "/" + endpoint.getDatabase()
				+ String.format("?useSSL=%b&trustServerCertificate=%b",
						new Object[] {Boolean.valueOf(endpoint.isUseSsl()),
								Boolean.valueOf(endpoint.isUseSsl())}));
		this.hikariDataSource.setUsername(this.config.getString("username"));
		this.hikariDataSource.setPassword(this.config.getString("password"));
		this.hikariDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		int maxPoolSize = this.config.getInt("connectionMaxPoolSize");
		this.hikariDataSource.setMaximumPoolSize(maxPoolSize);
		this.hikariDataSource
				.setMinimumIdle(Math.min(maxPoolSize, this.config.getInt("connectionMinPoolSize")));
		this.hikariDataSource.setConnectionTimeout(this.config.getInt("connectionTimeout"));
		this.hikariDataSource.setValidationTimeout(this.config.getInt("validationTimeout"));
		this.hikariDataSource.validate();
		return true;
	}

	@Override
	public Database getDatabase(String name) {
		Preconditions.checkNotNull(name);
		removedOutdatedEntries();
		if (!this.cachedDatabaseInstances.contains(name))
			this.cachedDatabaseInstances.add(name,
					Long.valueOf(System.currentTimeMillis() + NEW_CREATION_DELAY),
					new MySQLDatabase(this, name, this.executorService));
		return this.cachedDatabaseInstances.getSecond(name);
	}

	@Override
	public boolean deleteDatabase(String name) {
		Preconditions.checkNotNull(name);
		this.cachedDatabaseInstances.remove(name);
		if (containsDatabase(name))
			try {
				Connection connection = getConnection();
				try {
					PreparedStatement preparedStatement =
							connection.prepareStatement("DROP TABLE " + name);
					try {
						boolean bool = (preparedStatement.executeUpdate() != -1) ? true : false;
						if (preparedStatement != null)
							preparedStatement.close();
						if (connection != null)
							connection.close();
						return bool;
					} catch (Throwable throwable) {
						if (preparedStatement != null)
							try {
								preparedStatement.close();
							} catch (Throwable throwable1) {
								throwable.addSuppressed(throwable1);
							}
						throw throwable;
					}
				} catch (Throwable throwable) {
					if (connection != null)
						try {
							connection.close();
						} catch (Throwable throwable1) {
							throwable.addSuppressed(throwable1);
						}
					throw throwable;
				}
			} catch (SQLException exception) {
				exception.printStackTrace();
			}
		return false;
	}

	// @Override
	// public Collection<String> getDatabaseNames() {
	// return executeQuery(
	// "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='PUBLIC'",
	// resultSet -> {
	// Collection<String> collection = new ArrayList<>();
	// while (resultSet.next())
	// collection.add(resultSet.getString("table_name"));
	// return collection;
	// });
	// }
	@Override
	public Collection<String> getDatabaseNames() {
		try (Connection con = this.hikariDataSource.getConnection()) {
			ResultSet meta = con.getMetaData().getTables(null, null, null, TABLE_TYPE);
			Collection<String> names = new ArrayList<>();
			while (meta.next()) {
				names.add(meta.getString("table_name"));
			}
			return names;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new HashSet<>();
		}
		// return this.executeQuery(
		// "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='PUBLIC'",
		// resultSet -> {
		// Collection<String> collection = new ArrayList<>();
		// while (resultSet.next()) {
		// collection.add(resultSet.getString("table_name"));
		// }
		//
		// return collection;
		// });
	}

	@Override
	public String getName() {
		return this.config.getString("database");
	}

	@Override
	public void close() throws Exception {
		super.close();
		this.hikariDataSource.close();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.hikariDataSource.getConnection();
	}

	public HikariDataSource getHikariDataSource() {
		return this.hikariDataSource;
	}

	public JsonDocument getConfig() {
		return this.config;
	}

	public List<MySQLConnectionEndpoint> getAddresses() {
		return this.addresses;
	}

	@Override
	public int executeUpdate(String query, Object... objects) {
		Preconditions.checkNotNull(query);
		Preconditions.checkNotNull(objects);
		try {
			Connection connection = getConnection();
			try {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				try {
					int i = 1;
					for (Object object : objects)
						preparedStatement.setString(i++, object.toString());
					int j = preparedStatement.executeUpdate();
					if (preparedStatement != null)
						preparedStatement.close();
					if (connection != null)
						connection.close();
					return j;
				} catch (Throwable throwable) {
					if (preparedStatement != null)
						try {
							preparedStatement.close();
						} catch (Throwable throwable1) {
							throwable.addSuppressed(throwable1);
						}
					throw throwable;
				}
			} catch (Throwable throwable) {
				if (connection != null)
					try {
						connection.close();
					} catch (Throwable throwable1) {
						throwable.addSuppressed(throwable1);
					}
				throw throwable;
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
			return -1;
		}
	}

	@Override
	public <T> T executeQuery(String query, IThrowableCallback<ResultSet, T> callback,
			Object... objects) {
		Preconditions.checkNotNull(query);
		Preconditions.checkNotNull(callback);
		Preconditions.checkNotNull(objects);
		try {
			Connection connection = getConnection();
			try {
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				try {
					int i = 1;
					for (Object object : objects)
						preparedStatement.setString(i++, object.toString());
					ResultSet resultSet = preparedStatement.executeQuery();
					try {
						T object = callback.call(resultSet);
						if (resultSet != null)
							resultSet.close();
						if (preparedStatement != null)
							preparedStatement.close();
						if (connection != null)
							connection.close();
						return object;
					} catch (Throwable throwable) {
						if (resultSet != null)
							try {
								resultSet.close();
							} catch (Throwable throwable1) {
								throwable.addSuppressed(throwable1);
							}
						throw throwable;
					}
				} catch (Throwable throwable) {
					if (preparedStatement != null)
						try {
							preparedStatement.close();
						} catch (Throwable throwable1) {
							throwable.addSuppressed(throwable1);
						}
					throw throwable;
				}
			} catch (Throwable throwable) {
				if (connection != null)
					try {
						connection.close();
					} catch (Throwable throwable1) {
						throwable.addSuppressed(throwable1);
					}
				throw throwable;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
}
