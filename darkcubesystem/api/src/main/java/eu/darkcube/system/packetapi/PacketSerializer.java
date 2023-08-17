/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.packetapi;

import eu.cloudnetservice.driver.network.buffer.DataBuf;

public class PacketSerializer {

    public static DataBuf.Mutable serialize(Packet packet, DataBuf.Mutable buf) {
        if (packet != null) {
            return buf.writeString(packet.getClass().getName()).writeObject(packet);
        }
        return buf;
    }

    public static Packet readPacket(DataBuf buf) {
        Class<? extends Packet> cls = getClass(buf);
        return buf.readObject(cls);
    }

    public static Class<? extends Packet> getClass(DataBuf doc) {
        try {
            return (Class<? extends Packet>) Class.forName(doc.readString());
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
