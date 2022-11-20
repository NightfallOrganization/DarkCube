package eu.darkcube.minigame.woolbattle.util;

public enum TimeUnit {

	TICKS(50), SECOND(1000)


	;

	private final long millis;

	private TimeUnit(long millis) {
		this.millis = millis;
	}

	public long toUnit(TimeUnit other) {
		return millis / other.millis;
	}

	public long toMillis() {
		return this.millis;
	}
}
