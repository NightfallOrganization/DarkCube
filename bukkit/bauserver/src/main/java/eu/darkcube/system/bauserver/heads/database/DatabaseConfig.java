/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.database;

public record DatabaseConfig(String username, String password, ConnectionEndpoint endpoint) {
    public static final DatabaseConfig DEFAULT = new DatabaseConfig("root", "password", new ConnectionEndpoint("database", new HostAndPort("127.0.0.1", 3306)));
}
