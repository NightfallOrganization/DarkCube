package eu.darkcube.system.commandapi.v2;

import java.util.Properties;

public abstract class Command {

	private String[] names;
	private String permission;
	private String description;
	private String usage;
	private String prefix;

	public Command(String... names) {
		this.names = names;
	}

	public Command(String[] names, String permission) {
		this.names = names;
		this.permission = permission;
	}

	public Command(String[] names, String permission, String description) {
		this.names = names;
		this.permission = permission;
		this.description = description;
	}

	public Command(String[] names, String permission, String description, String usage, String prefix) {
		this.names = names;
		this.permission = permission;
		this.description = description;
		this.usage = usage;
		this.prefix = prefix;
	}

	public String getDescription() {
		return description;
	}

	public String[] getNames() {
		return names;
	}

	public String getPermission() {
		return permission;
	}

	public String getUsage() {
		return usage;
	}

	public String getPrefix() {
		return prefix;
	}

	public abstract void execute(ICommandExecutor executor, String command, String[] args, String commandLine,
			Properties properties);
}
