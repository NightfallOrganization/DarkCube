/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.bukkit.entity.Player;

public class I18n {
	private static final Locale[] LANGS = new Locale[] {
			Locale.GERMAN, Locale.ENGLISH
	};
//	private static final File folder = new File(Main.getInstance().getDataFolder(), "languages");
//	private static final File folder = new File("languages");
	private static Map<String, Map<Locale, String>> properties = new HashMap<>();
	private static Map<Player, Locale> players = new HashMap<>();

	public static void setPlayerLanguage(Player p, Locale locale) {
		players.put(p, locale);
	}

	public static void unloadPlayerLanguage(Player p) {
		players.remove(p);
	}

	public static Locale getPlayerLanguage(Player p) {
		return players.getOrDefault(p, LANGS[0]);
	}

	public static void init() {

	}

	static {
//		folder.mkdirs();
		String[] bundles = new String[] {
				"messages", "shopitems"
		};
		
		for (Locale loc : LANGS) {
			for(String bundle : bundles) {
				try {
					ResourceBundle b = ResourceBundle.getBundle(bundle, loc);
					for(String key : b.keySet()) {
						if(!properties.containsKey(key)) {
							properties.put(key, new HashMap<>());
						}
						properties.get(key).put(loc, b.getString(key));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
//		List<File> wantedFiles = new ArrayList<>();
//		for (Locale loc : LANGS) {
//			wantedFiles.add(new File(folder, "messages_" + loc.toLanguageTag() + ".lang"));
//			wantedFiles.add(new File(folder, "shopitems_" + loc.toLanguageTag() + ".lang"));
//		}
//		for (File f : wantedFiles) {
//			if (!f.exists()) {
//				try {
//					InputStream in = I18n.class.getClassLoader().getResourceAsStream(f.getName());
//					BufferedReader r = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8));
//					String line;
//					FileWriter w = new FileWriter(f);
//					while ((line = r.readLine()) != null) {
//						w.write(line);
//						w.write(System.lineSeparator());
//					}
//					w.flush();
//					w.close();
//					r.close();
//				} catch (FileNotFoundException ex) {
//					ex.printStackTrace();
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//
//		for (File file : folder.listFiles()) {
//			String name = file.getName();
//			if (name.endsWith(".lang")) {
//				name = name.replace(".lang", "");
//				boolean suc = false;
//				for (Locale lang : LANGS) {
//					if (name.equals("messages_" + lang.toLanguageTag())
//							|| name.equals("shopitems_" + lang.toLanguageTag())) {
//						try {
//							Reader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//							ResourceBundle bundle = new PropertyResourceBundle(in);
//							for (String key : bundle.keySet()) {
//								if (!properties.containsKey(key)) {
//									properties.put(key, new HashMap<>());
//								}
//								properties.get(key).put(lang, bundle.getString(key));
//							}
//							in.close();
//						} catch (MalformedURLException ex) {
//							ex.printStackTrace();
//						} catch (FileNotFoundException ex) {
//							ex.printStackTrace();
//						} catch (IOException ex) {
//							ex.printStackTrace();
//						}
//						suc = true;
//						break;
//					}
//				}
//				if (!suc) {
//					Main.getInstance()
//							.sendConsole("§cLanguage file " + file.getName() + " is not for a valid language!");
//				}
//			}
//		}
	}

	public static String translate(Locale locale, String key) {
		if (!properties.containsKey(key)) {
			return null;
		}
		return properties.get(key).get(locale);
	}
}
