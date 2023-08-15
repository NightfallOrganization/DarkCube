/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version.v1_8_8.provider.via;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.mapping.PacketMapping;
import com.viaversion.viaversion.api.protocol.packet.mapping.PacketMappings;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.TabCompleteTracker;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.provider.via.AbstractViaSupport;
import eu.darkcube.system.util.ReflectionUtils;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ViaSupport1_8_8 extends AbstractViaSupport {

    public static final String TAB_COMPLETE_CANCEL = "DarkCube:ViaSupport:TabCompletionFor1_13+:Cancel";

    public void enable() {
        DarkCubeSystem.systemPlugin().sendConsole("[ViaSupport] Enabling ViaVersion support");
        Protocol1_13To1_12_2 protocol = Via.getManager().getProtocolManager().getProtocol(Protocol1_13To1_12_2.class);
        PacketMappings clientboundMappings = ReflectionUtils.getValue(protocol, AbstractProtocol.class, true, "clientboundMappings", PacketMappings.class);
        ClientboundPackets1_12_1 unmapped = ClientboundPackets1_12_1.TAB_COMPLETE;
        ClientboundPackets1_13 mapped = ClientboundPackets1_13.TAB_COMPLETE;
        PacketMapping mapping = clientboundMappings.mappedPacket(unmapped.state(), unmapped.getId());
        PacketHandler oldHandler = mapping.handler();
        protocol.registerClientbound(unmapped, mapped, wrapper -> {
            TabCompleteTracker tabCompleteTracker = wrapper.user().get(TabCompleteTracker.class);
            int count = wrapper.read(Type.VAR_INT);
            c:
            if (count == 1) {
                String completion = wrapper.read(Type.STRING);
                if (completion.startsWith(TAB_COMPLETE_CANCEL)) {
                    int id;
                    try {
                        id = Integer.parseInt(completion.substring(TAB_COMPLETE_CANCEL.length()));
                    } catch (Throwable t) {
                        id = -1;
                    }
                    if (id == -1 || id > 15) {
                        wrapper.write(Type.VAR_INT, 0);
                        break c;
                    }
                    ViaTabExecutor.Data data = ViaTabExecutor.take(id);
                    if (data == null) {
                        wrapper.write(Type.VAR_INT, 0);
                        break c;
                    }
                    wrapper.write(Type.VAR_INT, tabCompleteTracker.getTransactionId());
                    wrapper.write(Type.VAR_INT, data.start());
                    wrapper.write(Type.VAR_INT, data.length());
                    wrapper.write(Type.VAR_INT, data.suggestions().length);
                    for (int i = 0; i < data.suggestions().length; i++) {
                        String suggestion = data.suggestions()[i];
                        Component tooltip = data.tooltips()[i];
                        wrapper.write(Type.STRING, suggestion);
                        wrapper.write(Type.BOOLEAN, tooltip != Component.empty());
                        if (tooltip != Component.empty()) wrapper.write(Type.STRING, GsonComponentSerializer.gson().serialize(tooltip));
                    }
                    return;
                } else {
                    wrapper.write(Type.VAR_INT, count);
                    wrapper.write(Type.STRING, completion);
                }
            } else {
                wrapper.write(Type.VAR_INT, count);
                for (int i = 0; i < count; i++) {
                    wrapper.passthrough(Type.STRING);
                }
            }

            wrapper.resetReader();
            oldHandler.handle(wrapper);
        }, true);
    }

    @Override public void init() {

    }

    @Override
    public List<String> tabComplete(int playerVersion, Player player, String commandLine, ParseResults<CommandSource> parse, Suggestions suggestions) {
        int id = ViaTabExecutor.work(commandLine, suggestions);
        return Collections.singletonList(TAB_COMPLETE_CANCEL + id);
    }
}
