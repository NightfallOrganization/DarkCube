/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.version.ViaSupport;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
public class InternalCommandTabExecutor implements TabExecutor {
    private static String sjoin(String label, String[] args) {
        return args.length == 0 ? label : (label + " " + String.join(" ", args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        String commandLine = sjoin(label, args);
        String commandLine2 = sjoin(label, Arrays.copyOfRange(args, 0, args.length - 1)) + " ";
        List<Suggestion> completions = eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands().getTabCompletions(sender, commandLine);
        ICommandExecutor executor = new BukkitCommandExecutor(sender);
        if (!completions.isEmpty()) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                int version = ViaSupport.getVersion(p);
                if (version < 393) { // 393 is the version for 1.13
                    // https://minecraft.fandom.com/wiki/Protocol_version
                    executor.sendMessage(Component.text(" "));
                    executor.sendCompletions(commandLine, completions);
                } else {
                    UserConnection con = Via.getManager().getConnectionManager().getConnectedClient(p.getUniqueId());
                    Protocol<?, ?, ?, ?> protocol = Via.getManager().getProtocolManager().getProtocol(version, 393);
                    PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_13.TAB_COMPLETE, con);
                    try {
                        protocol.transform(Direction.CLIENTBOUND, State.PLAY, wrapper);
                        wrapper.scheduleSendRaw();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Collections.emptyList();
                }
            }
        }
        List<String> r = new ArrayList<>();
        for (Suggestion completion : completions) {
            String c = completion.apply(commandLine);
            if (!c.startsWith(commandLine2)) continue;
            r.add(c.substring(commandLine2.length()));
        }
        return r;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        String commandLine = sjoin(label, args);
        eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands().executeCommand(sender, commandLine);
        return true;
    }
}
