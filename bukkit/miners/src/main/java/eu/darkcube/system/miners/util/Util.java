/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.util;

public class Util {

	public static String secondsToTime(int seconds, boolean showZeroHours, boolean showZeroMins,
			String color1, String color2) {
		if (color1 == null || color2 == null)
			return "Color null";
		if (seconds < 0)
			return "seconds<0";
		int minutes = 0;
		int hours = 0;
		StringBuilder returnString = new StringBuilder(color1);
		if (seconds >= 60) { // only calc mins if secs >60
			minutes = (seconds - (seconds % 60)) / 60;
			seconds = seconds % 60;
		}
		if (minutes >= 60 || showZeroHours) {
			hours = (minutes - (minutes % 60)) / 60;
			minutes = minutes % 60;
			if (hours < 10) {
				if (hours != 0 || showZeroHours) {
					returnString.append(0).append(hours);
				}
			} else {
				returnString.append(hours);
			}
			returnString.append(color2).append(":").append(color1);
		}
		if (minutes < 10) {
			if (minutes != 0 || showZeroMins || showZeroHours || hours != 0) {
				returnString.append(0).append(minutes).append(color2).append(":").append(color1);
			}
		} else {
			returnString.append(minutes).append(color2).append(":").append(color1);
		}
		if (seconds < 10) {
			returnString.append(0).append(seconds);
		} else {
			returnString.append(seconds);
		}
		return returnString.toString();
	}

	public static String secondsToTime(int seconds, boolean showZeroHours, boolean showZeroMins) {
		return secondsToTime(seconds, showZeroHours, showZeroMins, "", "");
	}

}
