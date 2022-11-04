package eu.darkcube.system.commandapi.v2;

import java.util.Properties;

public class PropertyHelper {

	public static Properties parse(String[] args) {
		Properties properties = new Properties();
		for (String argument : args) {
			if (argument.isEmpty() || argument.equals(" ")) {
				continue;
			}

			if (argument.contains("=")) {
				int x = argument.indexOf("=");
				properties.put(argument.substring(0, x).replaceFirst("-", "").replaceFirst("-", ""),
						argument.substring(x + 1));
				continue;
			}

			if (argument.contains("--") || argument.contains("-")) {
				properties.put(argument.replaceFirst("-", "").replaceFirst("-", ""), "true");
				continue;
			}

			properties.put(argument, "true");
		}
		return properties;
	}
}
