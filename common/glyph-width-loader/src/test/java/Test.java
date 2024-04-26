/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

import eu.darkcube.system.glyphwidthloader.GlyphWidthManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("C:\\Users\\dasba\\Documents\\Development\\DarkCube\\bukkit\\citybuild\\build\\resources\\generated\\glyph-widths.bin");
        GlyphWidthManager manager = new GlyphWidthManager();
        manager.loadGlyphData(path);
        System.out.println(manager.width(" "));
    }
}
