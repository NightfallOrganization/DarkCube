package eu.darkcube.minigame.smash.state;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.listener.BaseListener;

public abstract class GameState {

	private final Main main = Main.getInstance();
	private boolean enabled = false;
	private BaseListener[] listeners;
	
	public GameState(BaseListener... listeners) {
		this.listeners = listeners;
	}
	
	protected abstract void onEnable();
	protected abstract void onDisable();
	
	public final void enable() {
		if(!enabled) {
			enabled = true;
			onEnable();
			for(BaseListener listener : listeners) {
				listener.register();
			}
		}
	}
	
	public final void disable() {
		if(enabled) {
			enabled = false;
			onDisable();
			for(BaseListener listener : listeners) {
				listener.unregister();
			}
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	protected Main getMain() {
		return main;
	}
}
