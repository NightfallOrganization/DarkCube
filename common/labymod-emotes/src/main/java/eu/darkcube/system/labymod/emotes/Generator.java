/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.labymod.emotes;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Generator {

	public static void main(String[] args) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(
				Generator.class.getClassLoader().getResourceAsStream("input"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read;
		byte[] buf = new byte[1024];
		while ((read = bin.read(buf)) != -1) {
			out.write(buf, 0, read);
		}
		String data = new String(out.toByteArray(), StandardCharsets.UTF_8);
		System.out.println(Generator.generate(data));
		bin.close();
	}

	/**
	 * Converts the emotes from <a href=
	 * "https://docs.labymod.net/pages/server/labymod/emote_api/">docs.labymod.net/pages/server/labymod/emote_api</a>
	 * 
	 * @param input
	 * @return the enum data
	 */
	public static String generate(String input) {
		StringBuilder b = new StringBuilder();
		for (String line : input.split("\\r?\\n")) {
			if (line.isEmpty())
				continue;
			String[] data = line.split(" +", 2);
			int id = Integer.parseInt(data[0]);
			String name = data[1].toUpperCase().replace(' ', '_').replace("<STOP_EMOTE>", "STOP_EMOTE").replace('-', '_');
			b.append(name).append("(").append(id).append("),");
		}
		return b.toString();
	}

}
