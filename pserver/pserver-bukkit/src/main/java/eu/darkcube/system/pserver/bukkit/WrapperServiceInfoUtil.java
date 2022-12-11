/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit;

import eu.darkcube.system.pserver.common.ServiceInfoUtil;

public class WrapperServiceInfoUtil extends ServiceInfoUtil {

	static {
		new WrapperServiceInfoUtil();
	}

	public static void init() {

	}
}
