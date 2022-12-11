/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.mysql;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Result {
	default Result goTo(int line, int column) {
		return goToLine(line).goToColumn(column);
	}

	ResultSet raw();

	boolean isEmpty();

	Result goToLine(int line);

	Result goToColumn(int column);

	int getInt() throws SQLException;

	byte getByte() throws SQLException;

	Date getDate() throws SQLException;

	Array getArray() throws SQLException;

	String getString() throws SQLException;

	double getDouble() throws SQLException;

	Object getObject() throws SQLException;

	boolean getBoolean() throws SQLException;

	BigDecimal getBigDecimal() throws SQLException;

	@Override
	String toString();
}
