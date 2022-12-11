/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.statsreset.mysql;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLResult extends MySQLUtils implements Result {
	private int column;
	private ResultSet res;
	private MySQL mysql;

	protected MySQLResult(MySQL mysql, ResultSet res) {
		super(res);
		column = 1;
		this.mysql = mysql;
		this.res = res;
	}

	@Override
	public byte getByte() throws SQLException {
		return getByte(column);
	}

	@Override
	public Date getDate() throws SQLException {
		return getDate(column);
	}

	@Override
	public Array getArray() throws SQLException {
		return getArray(column);
	}

	@Override
	public double getDouble() throws SQLException {
		return getDouble(column);
	}

	@Override
	public int getInt() throws SQLException {
		return getInt(column);
	}

	@Override
	public BigDecimal getBigDecimal() throws SQLException {
		return getBigDecimal(column);
	}

	@Override
	public Object getObject() throws SQLException {
		return getObject(column);
	}

	@Override
	public boolean getBoolean() throws SQLException {
		return getBoolean(column);
	}

	@Override
	public String getString() throws SQLException {
		return getString(column);
	}

	@Override
	public MySQLResult goToLine(int line) {
		try {
			res.beforeFirst();
			int i = 0;
			for (; i < line && res.next(); i++)
				;
			if (i + 1 != line)
				throw new ArrayIndexOutOfBoundsException();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return this;
	}

	@Override
	public MySQLResult goToColumn(int column) {
		this.column = column;
		return this;
	}

	@Override
	public String toString() {
		try {
			return mysql.toString(res);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@Override
	public boolean isEmpty() {
		try {
			return !res.first();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public ResultSet raw() {
		return res;
	}

}
