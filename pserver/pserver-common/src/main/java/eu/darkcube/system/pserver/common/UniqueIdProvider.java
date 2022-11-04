package eu.darkcube.system.pserver.common;

public abstract class UniqueIdProvider {

	private static UniqueIdProvider instance;
	
	public UniqueIdProvider() {
		instance = this;
	}
	
	public abstract boolean isAvailable(UniqueId id);
	
	public abstract UniqueId newUniqueId();
	
	public static UniqueIdProvider getInstance() {
		return instance;
	}
}
