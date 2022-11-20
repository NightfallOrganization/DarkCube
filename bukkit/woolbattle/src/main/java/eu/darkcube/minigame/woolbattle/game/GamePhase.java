package eu.darkcube.minigame.woolbattle.game;

public abstract class GamePhase {

	private boolean enabled = false;

	protected abstract void onEnable();

	protected abstract void onDisable();

	public boolean isEnabled() {
		return this.enabled;
	}

	public void enable() {
		if (!this.enabled) {
			this.enabled = true;
			this.onEnable();
		}
	}

	public void disable() {
		if (this.enabled) {
			this.enabled = false;
			this.onDisable();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T cast() {
		return (T) this;
	}

}
