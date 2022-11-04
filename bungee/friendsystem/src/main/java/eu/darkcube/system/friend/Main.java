package eu.darkcube.system.friend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.darkcube.system.friend.command.CommandFriend;
import eu.darkcube.system.friend.command.CommandMessage;
import eu.darkcube.system.friend.command.CommandReact;
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
		getProxy().getPluginManager().registerCommand(this, new CommandFriend());
		getProxy().getPluginManager().registerCommand(this, new CommandMessage());
		getProxy().getPluginManager().registerCommand(this, new CommandReact());
		getProxy().getPluginManager().registerListener(this, new Listener());
	}

	@Override
	public void onDisable() {
	}
	
	public static void sendMessage(CommandSender p, String msg) {
		sendMessage(p, new TextComponent(msg));
	}

	public static void sendMessage(CommandSender p, BaseComponent... msg) {
		List<BaseComponent> ls = new ArrayList<>();
		ls.add(new TextComponent("§8[§4Friends§8] "));
		ls.addAll(Arrays.asList(msg));
		p.sendMessage(ls.toArray(new BaseComponent[0]));
	}
	
	public static Main getInstance() {
		return instance;
	}
}
