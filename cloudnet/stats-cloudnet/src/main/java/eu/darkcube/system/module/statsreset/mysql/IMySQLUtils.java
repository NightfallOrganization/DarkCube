/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.statsreset.mysql;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.SQLException;

public interface IMySQLUtils {
	int getInt(int row, int column) throws SQLException;

	byte getByte(int row, int column) throws SQLException;

	Date getDate(int row, int column) throws SQLException;

	Array getArray(int row, int column) throws SQLException;

	String getString(int row, int column) throws SQLException;

	double getDouble(int row, int column) throws SQLException;

	Object getObject(int row, int column) throws SQLException;

	boolean getBoolean(int row, int column) throws SQLException;

	BigDecimal getBigDecimal(int row, int column) throws SQLException;
}
