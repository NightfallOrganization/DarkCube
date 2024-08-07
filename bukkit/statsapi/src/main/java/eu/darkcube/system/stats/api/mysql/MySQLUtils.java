/*
 * Copyright (c) 2022-2024. [DarkCube]
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

class MySQLUtils implements IMySQLUtils {
	private ResultSet res;

	protected MySQLUtils(ResultSet res) {
		this.res = res;
	}

	public double getDouble(int column) throws SQLException {
		return res.getDouble(column);
	}

	public Object getObject(int column) throws SQLException {
		return res.getObject(column);
	}

	public int getInt(int column) throws SQLException {
		return res.getInt(column);
	}

	public Array getArray(int column) throws SQLException {
		return res.getArray(column);
	}

	public byte getByte(int column) throws SQLException {
		return res.getByte(column);
	}

	public boolean getBoolean(int column) throws SQLException {
		return res.getBoolean(column);
	}

	public BigDecimal getBigDecimal(int column) throws SQLException {
		return res.getBigDecimal(column);
	}

	public Date getDate(int column) throws SQLException {
		return res.getDate(column);
	}

	public String getString(int column) throws SQLException {
		return res.getString(column);
	}

	private void goToLine(int row) {
		try {
			res.beforeFirst();
			int i = 0;
			for (; i < row && res.next(); i++)
				;
			if (i + 1 != row)
				throw new ArrayIndexOutOfBoundsException();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public int getInt(int row, int column) throws SQLException {
		goToLine(row);
		return getInt(column);
	}

	@Override
	public byte getByte(int row, int column) throws SQLException {
		goToLine(row);
		return getByte(column);
	}

	@Override
	public Date getDate(int row, int column) throws SQLException {
		goToLine(row);
		return getDate(column);
	}

	@Override
	public Array getArray(int row, int column) throws SQLException {
		goToLine(row);
		return getArray(column);
	}

	@Override
	public String getString(int row, int column) throws SQLException {
		goToLine(row);
		return getString(column);
	}

	@Override
	public double getDouble(int row, int column) throws SQLException {
		goToLine(row);
		return getDouble(column);
	}

	@Override
	public Object getObject(int row, int column) throws SQLException {
		goToLine(row);
		return getObject(column);
	}

	@Override
	public boolean getBoolean(int row, int column) throws SQLException {
		goToLine(row);
		return getBoolean(column);
	}

	@Override
	public BigDecimal getBigDecimal(int row, int column) throws SQLException {
		goToLine(row);
		return getBigDecimal(column);
	}

}
