/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.List;
import java.util.stream.Collectors;

public class AdventureUtils_1_19_2 {

	public static Component convert(
			eu.darkcube.system.libs.net.kyori.adventure.text.Component component) {
		return GsonComponentSerializer.gson().deserialize(
				eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson()
						.serialize(component));
	}

	public static List<Component> convert1(
			List<eu.darkcube.system.libs.net.kyori.adventure.text.Component> components) {
		return components.stream().map(AdventureUtils_1_19_2::convert).collect(Collectors.toList());
	}

	public static List<eu.darkcube.system.libs.net.kyori.adventure.text.Component> convert2(
			List<Component> components) {
		return components.stream().map(AdventureUtils_1_19_2::convert).collect(Collectors.toList());
	}

	public static eu.darkcube.system.libs.net.kyori.adventure.text.Component convert(
			Component component) {
		return eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson()
				.deserialize(GsonComponentSerializer.gson().serialize(component));
	}

}
