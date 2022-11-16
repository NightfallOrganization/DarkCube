package eu.darkcube.minigame.woolbattle.perk;

public class PerkName {

	public static final PerkName CAPSULE = new PerkName("CAPSULE");
	public static final PerkName SWITCHER = new PerkName("SWITCHER");
	public static final PerkName LINE_BUILDER = new PerkName("LINE_BUILDER");
	public static final PerkName WOOL_BOMB = new PerkName("WOOL_BOMB");
	public static final PerkName RONJAS_TOILET_SPLASH = new PerkName(
					"RONJAS_TOILET_SPLASH");
	public static final PerkName BLINK = new PerkName("BLINK");
	public static final PerkName SAFETY_PLATFORM = new PerkName(
					"SAFETY_PLATFORM");
	public static final PerkName WALL_GENERATOR = new PerkName(
					"WALL_GENERATOR");
	public static final PerkName GRANDPAS_CLOCK = new PerkName(
					"GRANDPAS_CLOCK");
	public static final PerkName GHOST = new PerkName("GHOST");
	public static final PerkName GRABBER = new PerkName("GRABBER");
	public static final PerkName MINIGUN = new PerkName("MINIGUN");
	public static final PerkName BOOSTER = new PerkName("BOOSTER");
	public static final PerkName GRAPPLING_HOOK = new PerkName(
					"GRAPPLING_HOOK");
	public static final PerkName ROPE = new PerkName("ROPE");

	public static final PerkName EXTRA_WOOL = new PerkName("EXTRA_WOOL");
//	public static final PerkName DOUBLE_WOOL = new PerkName("DOUBLE_WOOL");
//	public static final PerkName BACKPACK = new PerkName("BACKPACK");
	public static final PerkName ROCKETJUMP = new PerkName("ROCKETJUMP");
	public static final PerkName ARROW_RAIN = new PerkName("ARROW_RAIN");
	public static final PerkName FAST_ARROW = new PerkName("FAST_ARROW");
	public static final PerkName TNT_ARROW = new PerkName("TNT_ARROW");
	public static final PerkName ELEVATOR = new PerkName("ELEVATOR");
	public static final PerkName LONGJUMP = new PerkName("LONGJUMP");

	public static final PerkName SLIME_PLATFORM = new PerkName("SLIME_PLATFORM");
	private final String name;

	public PerkName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public PerkType toType() {
		return PerkType.valueOf(this);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof PerkName
						&& ((PerkName) o).getName().equals(this.getName()))
						|| o != null && o.toString().equals(this.getName());
	}

	@Override
	public String toString() {
		return this.getName();
	}
}