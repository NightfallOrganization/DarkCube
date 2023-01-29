/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dytanic.cloudnet.ext.database.mysql.util;

import de.dytanic.cloudnet.driver.network.HostAndPort;

public final class MySQLConnectionEndpoint {

	protected final boolean useSsl;

	protected final String database;

	protected final HostAndPort address;

	@Override
	public String toString() {
		return "MySQLConnectionEndpoint(useSsl=" + isUseSsl() + ", database=" + getDatabase()
				+ ", address=" + getAddress() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof MySQLConnectionEndpoint))
			return false;
		MySQLConnectionEndpoint other = (MySQLConnectionEndpoint) o;
		if (isUseSsl() != other.isUseSsl())
			return false;
		Object this$database = getDatabase(), other$database = other.getDatabase();
		if ((this$database == null) ? (other$database != null)
				: !this$database.equals(other$database))
			return false;
		Object this$address = getAddress(), other$address = other.getAddress();
		return !((this$address == null) ? (other$address != null)
				: !this$address.equals(other$address));
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = result * 59 + (isUseSsl() ? 79 : 97);
		Object $database = getDatabase();
		result = result * 59 + (($database == null) ? 43 : $database.hashCode());
		Object $address = getAddress();
		return result * 59 + (($address == null) ? 43 : $address.hashCode());
	}

	public MySQLConnectionEndpoint(boolean useSsl, String database, HostAndPort address) {
		this.useSsl = useSsl;
		this.database = database;
		this.address = address;
	}

	public boolean isUseSsl() {
		return this.useSsl;
	}

	public String getDatabase() {
		return this.database;
	}

	public HostAndPort getAddress() {
		return this.address;
	}

}
