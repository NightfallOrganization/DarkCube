package eu.darkcube.system.pserver.plugin.link.luckperms;

import eu.darkcube.system.pserver.plugin.link.Link;
import net.luckperms.api.LuckPermsProvider;

public class LuckPermsLink extends Link {

	private CustomContextCalculator contextCalculator;

	public LuckPermsLink() throws Throwable {
		super();
	}

	@Override
	protected void link() throws Throwable {
		contextCalculator = new CustomContextCalculator();
		LuckPermsProvider.get().getContextManager().registerCalculator(contextCalculator);
	}

	@Override
	protected void unlink() {
		LuckPermsProvider.get().getContextManager().unregisterCalculator(contextCalculator);
		contextCalculator = null;
	}
}
