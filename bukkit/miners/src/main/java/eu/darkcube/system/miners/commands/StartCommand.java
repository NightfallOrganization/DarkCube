package eu.darkcube.system.miners.commands;

import eu.darkcube.system.miners.gamephase.lobbyphase.LobbyTimer;

public class StartCommand extends MinersCommand {

    public StartCommand() {
        super("start",builder-> builder.executes(context -> {

            LobbyTimer.lobbyTime = 0;

            return 0;
        }));
    }

}
