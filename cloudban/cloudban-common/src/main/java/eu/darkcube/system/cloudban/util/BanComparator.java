/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util;

import java.util.Comparator;

import eu.darkcube.system.cloudban.util.ban.Ban;

class BanComparator implements Comparator<Ban> {
	@Override
	public int compare(Ban o1, Ban o2) {
		return o1.getDuration().compareTo(o2.getDuration());
	}
	
}
