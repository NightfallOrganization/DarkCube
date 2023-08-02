/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import org.bukkit.entity.Player;

class ViaTabExecutor {
    public static void work(int version, Player p) {
        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_13.TAB_COMPLETE, Via.getManager().getConnectionManager().getConnectedClient(p.getUniqueId()));
        try {
            Via.getManager().getProtocolManager().getProtocol(version, 393).transform(Direction.CLIENTBOUND, State.PLAY, wrapper);
            wrapper.scheduleSendRaw();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
