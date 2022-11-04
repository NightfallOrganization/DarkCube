package eu.darkcube.minigame.smash.character;

import eu.darkcube.minigame.smash.smash.AttackSmash;
import eu.darkcube.minigame.smash.smash.BaseSmash;
import eu.darkcube.minigame.smash.smash.DownSmash;
import eu.darkcube.minigame.smash.smash.FrontSmash;
import eu.darkcube.minigame.smash.smash.JumpSmash;
import eu.darkcube.minigame.smash.smash.ShieldSmash;
import eu.darkcube.minigame.smash.smash.UpSmash;
import eu.darkcube.minigame.smash.smash.kirby.KirbyAttackSmash;
import eu.darkcube.minigame.smash.smash.kirby.KirbyBaseSmash;
import eu.darkcube.minigame.smash.smash.kirby.KirbyDownSmash;
import eu.darkcube.minigame.smash.smash.kirby.KirbyFrontSmash;
import eu.darkcube.minigame.smash.smash.kirby.KirbyJumpSmash;
import eu.darkcube.minigame.smash.smash.kirby.KirbyUpSmash;
import eu.darkcube.minigame.smash.user.User;

public enum Character {

	KIRBY("kirby", "Kirby", new KirbyAttackSmash(), new KirbyBaseSmash(), new KirbyDownSmash(), new KirbyFrontSmash(),
			new KirbyJumpSmash(), new ShieldSmash(), new KirbyUpSmash(), 1.3F);

	;

	private String sbTeamName;
	private String sbPrefix;
	private AttackSmash attackSmash;
	private BaseSmash baseSmash;
	private DownSmash downSmash;
	private FrontSmash frontSmash;
	private JumpSmash jumpSmash;
	private ShieldSmash shieldSmash;
	private UpSmash upSmash;
	private float speed;

	private Character(String sbTeamName, String sbPrefix, AttackSmash attackSmash, BaseSmash baseSmash,
			DownSmash downSmash, FrontSmash frontSmash, JumpSmash jumpSmash, ShieldSmash shieldSmash, UpSmash upSmash, float speed) {
		this.sbTeamName = sbTeamName;
		this.sbPrefix = "§e" + (sbPrefix.length() > 11 ? sbPrefix.substring(0, 11) : sbPrefix) + "§7 ";
		this.attackSmash = attackSmash;
		this.baseSmash = baseSmash;
		this.downSmash = downSmash;
		this.frontSmash = frontSmash;
		this.jumpSmash = jumpSmash;
		this.shieldSmash = shieldSmash;
		this.upSmash = upSmash;
		this.speed = speed;
	}
	
	public void resetSpeedMultiplier(User user) {
		user.setSpeedMultiplier(speed);
	}

	public String getSbPrefix() {
		return sbPrefix;
	}

	public String getSbTeamName() {
		return sbTeamName;
	}

	public BaseSmash getBaseSmash() {
		return baseSmash;
	}

	public JumpSmash getJumpSmash() {
		return jumpSmash;
	}

	public ShieldSmash getShieldSmash() {
		return shieldSmash;
	}

	public UpSmash getUpSmash() {
		return upSmash;
	}

	public AttackSmash getAttackSmash() {
		return attackSmash;
	}

	public DownSmash getDownSmash() {
		return downSmash;
	}

	public FrontSmash getFrontSmash() {
		return frontSmash;
	}

}
