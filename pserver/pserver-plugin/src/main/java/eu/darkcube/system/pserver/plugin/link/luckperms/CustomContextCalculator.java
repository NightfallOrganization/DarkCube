/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.plugin.link.luckperms;

import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.MutableContextSet;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CustomContextCalculator implements ContextCalculator<Player> {

	@Override
	public void calculate(@NonNull Player target, @NonNull ContextConsumer consumer) {
		consumer.accept("pserver", Boolean.toString(PServerProvider.getInstance().isPServer()));
		if (PServerProvider.getInstance().isPServer()) {
			PServerExecutor ps = PServerProvider.getInstance().getCurrentPServer();
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
