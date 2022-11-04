package eu.darkcube.system.cloudban.command;

public abstract class SubCommand extends Command {

	public SubCommand(String[] names, String usage, String permission, String prefix, String description) {
		super(names, usage, permission, prefix, description, false);
	}

	private String spaced;

	public void setSpaced(String spaced) {
		this.spaced = spaced;
	}

	public String getSpaced() {
		return spaced;
	}

}
