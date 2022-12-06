package eu.darkcube.system.pserver.cloudnet.database;

public abstract class Database {

	public final <T extends Database> T cast(Class<T> clazz) {
		return clazz.cast(this);
	}

}
