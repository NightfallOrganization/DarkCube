/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.console;

import java.io.IOException;
import java.util.List;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.listener.TabCompleteListener;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class ServerConsole {
    private static volatile boolean exit = false;
    private static volatile Terminal terminal;

    public static void init() {
        Thread.ofVirtual().name("ServerConsole").start(() -> {
            try {
                run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        synchronized (ServerConsole.class) { // Wait for terminal to be created
            try {
                ServerConsole.class.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        MinecraftServer.getSchedulerManager().buildShutdownTask(ServerConsole::stop);
    }

    public static void stop() {
        exit = true;
        if (terminal != null) {
            try {
                terminal.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            terminal = null;
        }
    }

    private static void run() throws Throwable {
        terminal = TerminalBuilder.terminal();
        var lineReader = LineReaderBuilder.builder().completer(new MinestomCompleter()).terminal(terminal).build();
        synchronized (ServerConsole.class) {
            ServerConsole.class.notify();
        }
        while (!exit) {
            try {
                var command = lineReader.readLine();
                MinecraftServer.getCommandManager().execute(MinecraftServer.getCommandManager().getConsoleSender(), command);
            } catch (UserInterruptException e) {
                MinecraftServer.stopCleanly();
                return;
            } catch (EndOfFileException e) {
                return;
            }
        }
    }

    private record MinestomCompleter() implements Completer {
        @Override public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            final var commandManager = MinecraftServer.getCommandManager();
            final var consoleSender = commandManager.getConsoleSender();
            if (line.wordIndex() == 0) {
                final var commandString = line.word().toLowerCase();
                candidates.addAll(commandManager
                        .getDispatcher()
                        .getCommands()
                        .stream()
                        .map(Command::getName)
                        .filter(name -> commandString.isBlank() || name.toLowerCase().startsWith(commandString))
                        .map(Candidate::new)
                        .toList());
            } else {
                final var text = line.line();
                final var suggestion = TabCompleteListener.getSuggestion(consoleSender, text);
                if (suggestion != null) {
                    suggestion.getEntries().stream().map(SuggestionEntry::getEntry).map(Candidate::new).forEach(candidates::add);
                }
            }
        }
    }
}
