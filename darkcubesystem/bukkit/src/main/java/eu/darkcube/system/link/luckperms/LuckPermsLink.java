/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.link.luckperms;

import eu.darkcube.system.link.Link;
import net.luckperms.api.LuckPermsProvider;

public class LuckPermsLink extends Link {
	private LinkContextCalculator calculator;

	public LuckPermsLink() throws Throwable {
		super();
	}

	@Override
	protected void link() throws Throwable {
		calculator = new LinkContextCalculator();
		LuckPermsProvider.get().getContextManager().registerCalculator(calculator);
	}

	@Override
	protected void unlink() {
		LuckPermsProvider.get().getContextManager().unregisterCalculator(calculator);
		calculator = null;
	}
}
