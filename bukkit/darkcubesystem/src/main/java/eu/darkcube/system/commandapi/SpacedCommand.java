package eu.darkcube.system.commandapi;

import org.bukkit.plugin.java.*;

public abstract class SpacedCommand extends Command implements ISpaced {

	public SpacedCommand(JavaPlugin plugin, String name, SubCommand[] childs, String beschreibung, Argument... arguments) {
		super(plugin, name, childs, beschreibung, arguments);
	}
	
	public SpacedCommand(JavaPlugin plugin, String name, SubCommand[] childs, String beschreibung) {
		super(plugin, name, childs, beschreibung);
	}

	private String spaced = "";

	@Override
	public void setSpaced(String spaced) {
		this.spaced = spaced;
		for (Command child : getChilds()) {
			((SubCommand) child).setSpaced(spaced);
		}
	}

	@Override
	public String getSpaced() {
		return spaced;
	}

	public static abstract class SubCommand extends Command {

		public SubCommand(JavaPlugin plugin, String name, Command[] childs, String beschreibung, Argument... arguments) {
			super(plugin, name, childs, beschreibung, arguments);
		}
		
		public SubCommand(JavaPlugin plugin, String name, Command[] childs, String beschreibung) {
			super(plugin, name, childs, beschreibung);
		}

		private String spaced = "";

		public String getSpaced() {
			return spaced;
		}

		public void setSpaced(String spaced) {
			this.spaced = spaced;
		}

	}

	public static abstract class SpacedSubCommand extends SubCommand implements ISpaced {

		public SpacedSubCommand(JavaPlugin plugin, String name, SubCommand[] childs, String beschreibung, Argument... arguments) {
			super(plugin, name, childs, beschreibung, arguments);
		}
		
		public SpacedSubCommand(JavaPlugin plugin, String name, SubCommand[] childs, String beschreibung) {
			super(plugin, name, childs, beschreibung);
		}

		private String spaced = "";

		@Override
		public void setSpaced(String spaced) {
			this.spaced = spaced;
			for (Command child : getChilds()) {
				((SubCommand) child).setSpaced(spaced);
			}
		}

		@Override
		public String getSpaced() {
			return spaced;
		}
	}
}
