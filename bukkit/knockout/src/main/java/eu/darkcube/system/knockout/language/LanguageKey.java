package eu.darkcube.system.knockout.language;

public enum LanguageKey {

	MAPSWITCH("mapswitch"),
	JOIN_MESSAGE("joinmessage"),
	QUIT_MESSAGE("quitmessage"),
	NOT_ENOUGH_COINS("coins.notenough"),
	COINS("coins.of"),
	COINS_OF_PLAYER("coins.of.player"),
	COINS_ADD("coins.add"),
	COINS_ADD_PLAYER("coins.add.player"),
	COINS_REMOVE("coins.remove"),
	COINS_REMOVE_PLAYER("coins.remove.player"),
	
	
	;
	private final String key;

	private LanguageKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
