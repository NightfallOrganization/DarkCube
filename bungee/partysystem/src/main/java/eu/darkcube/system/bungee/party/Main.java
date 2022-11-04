package eu.darkcube.system.bungee.party;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

	private static Main instance;

	public Main() {
		instance = this;
	}

	@Override
	public void onEnable() {

	}

	public static void sendMessage(CommandSender p, String msg) {
		sendMessage(p, TextComponent.fromLegacyText(msg));
	}

	public static void sendMessage(CommandSender p, BaseComponent... msg) {
//		List<BaseComponent> ls = new ArrayList<>();
//		ls.add(new TextComponent("§8[§4Party§8] §7"));
//		ls.addAll(Arrays.asList(msg));
//		p.sendMessage(ls.toArray(new BaseComponent[0]));
		CustomComponentBuilder b = new CustomComponentBuilder("");
		CustomComponentBuilder.applyPrefixModifier(c -> {
			c.append(CustomComponentBuilder.cast(TextComponent.fromLegacyText("§8[§4Party§8] §7")));
		}, CustomComponentBuilder.cast(msg)).accept(b);
		p.sendMessage(b.create());
	}

	public static Main getInstance() {
		return instance;
	}
}
