package eu.darkcube.system.miners.gamephase.miningphase;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.util.Timer;

public class Miningphase {

	private Timer miningTimer;

	public Miningphase() {

		miningTimer = new Timer() {

			@Override
			public void onIncrement() {
			}

			@Override
			public void onEnd() {
				// startNextPhase();
				System.out.println("init gamephase 2");
			}
		};
		miningTimer.start(Miners.getMinersConfig().MINING_PHASE_DURATION * 1000);

	}

}
