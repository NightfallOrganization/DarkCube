package eu.darkcube.minigame.smash.state.ingame;

import eu.darkcube.minigame.smash.listener.IngameBlock;
import eu.darkcube.minigame.smash.listener.IngameEntityDamage;
import eu.darkcube.minigame.smash.listener.IngameMoveClimbing;
import eu.darkcube.minigame.smash.listener.IngameSmashExecute;
import eu.darkcube.minigame.smash.listener.IngameSneakThroughBlocks;
import eu.darkcube.minigame.smash.state.GameState;

public class Ingame extends GameState {

	public Ingame() {
		super(new IngameEntityDamage(), new IngameMoveClimbing(), new IngameSmashExecute(), new IngameSneakThroughBlocks(), new IngameBlock());
	}
	
	@Override
	protected void onEnable() {
		
	}

	@Override
	protected void onDisable() {
		
	}
}
