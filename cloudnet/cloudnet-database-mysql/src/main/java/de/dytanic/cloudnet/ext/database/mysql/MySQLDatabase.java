/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dytanic.cloudnet.ext.database.mysql;

import java.util.concurrent.ExecutorService;
import de.dytanic.cloudnet.database.sql.SQLDatabase;

public final class MySQLDatabase extends SQLDatabase {

	public MySQLDatabase(MySQLDatabaseProvider databaseProvider, String name,
			ExecutorService executorService) {
		super(databaseProvider, name, executorService);
	}

	@Override
	public boolean isSynced() {
		return true;
	}
}
