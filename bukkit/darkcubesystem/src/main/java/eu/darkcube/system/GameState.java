package eu.darkcube.system;

public enum GameState {
	
	LOBBY,
	INGAME,
	STOPPING,
	UNKNOWN
	
	;
	
	public static GameState fromString(String gameState) {
		for(GameState state : values()) {
			if(state.toString().equals(gameState)) {
				return state;
			}
		}
		return GameState.UNKNOWN;
	}
	
	@Override
	public String toString() {
		return super.name();
	}
}