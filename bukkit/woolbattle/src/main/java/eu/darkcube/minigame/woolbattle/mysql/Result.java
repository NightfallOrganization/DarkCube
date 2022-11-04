package eu.darkcube.minigame.woolbattle.mysql;

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
