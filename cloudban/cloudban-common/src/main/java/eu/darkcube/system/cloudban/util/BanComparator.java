package eu.darkcube.system.cloudban.util;

import java.util.Comparator;

import eu.darkcube.system.cloudban.util.ban.Ban;

class BanComparator implements Comparator<Ban> {
	@Override
	public int compare(Ban o1, Ban o2) {
		return o1.getDuration().compareTo(o2.getDuration());
	}
	
}