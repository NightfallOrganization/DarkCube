package eu.darkcube.system.pserver.plugin.link.luckperms;

import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;

import eu.darkcube.system.pserver.common.*;
import net.luckperms.api.context.*;

public class CustomContextCalculator implements ContextCalculator<Player> {

	@Override
	public void calculate(@NonNull Player target, @NonNull ContextConsumer consumer) {
		consumer.accept("pserver", Boolean.toString(PServerProvider.getInstance().isPServer()));
		if (PServerProvider.getInstance().isPServer()) {
			PServer ps = PServerProvider.getInstance().getCurrentPServer();
			if (ps.getOwners().contains(target.getUniqueId())) {
				consumer.accept("pserverowner", Boolean.toString(true));
			} else {
				consumer.accept("pserverowner", Boolean.toString(false));
			}
		}
	}

	@Override
	public ContextSet estimatePotentialContexts() {
		MutableContextSet set = MutableContextSet.create();
		set.add("pserver", Boolean.toString(true));
		set.add("pserver", Boolean.toString(false));
		set.add("pserverowner", Boolean.toString(true));
		set.add("pserverowner", Boolean.toString(false));
		return set;
	}
}
