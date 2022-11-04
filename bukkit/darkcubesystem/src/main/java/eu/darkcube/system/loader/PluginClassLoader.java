package eu.darkcube.system.loader;

import java.nio.file.*;

/**
 * Represents the plugins classloader
 */
public interface PluginClassLoader {

    void loadJar(Path file);

}
