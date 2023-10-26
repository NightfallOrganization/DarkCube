/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment;

public class Test {
	public static void main(String[] args) {

		String s = "**test**hi**";
		for (String st : s.split("\\*\\*")) {
			System.out.println(st);
		}

	}
}
