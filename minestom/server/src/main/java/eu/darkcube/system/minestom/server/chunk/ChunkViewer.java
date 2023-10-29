/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.chunk;

public interface ChunkViewer<T> {

    void loadChunk(int chunkX, int chunkY, T chunk);

    void unloadChunk(int chunkX, int chunkY);

}
