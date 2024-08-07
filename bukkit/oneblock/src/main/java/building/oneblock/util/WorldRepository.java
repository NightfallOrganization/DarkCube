package building.oneblock.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class WorldRepository {

    /**
     * Speichert Daten in einer spezifischen Welt.
     * @param worldName Der Name der Welt, in der die Daten gespeichert werden sollen.
     * @param data Die Daten, die gespeichert werden sollen.
     * @throws IOException Wenn ein Fehler beim Schreiben der Daten auftritt.
     */
    public void saveWorldData(String worldName, List<String> data) throws IOException {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("Welt " + worldName + " nicht gefunden.");
        }

        File worldFolder = world.getWorldFolder();
        File dataFile = new File(worldFolder, "world_data.txt");

        Files.write(Paths.get(dataFile.getPath()), data);
    }

    /**
     * Liest Daten aus einer spezifischen Welt.
     * @param worldName Der Name der Welt, aus der die Daten gelesen werden sollen.
     * @return Eine Liste von Zeilen mit den gelesenen Daten.
     * @throws IOException Wenn ein Fehler beim Lesen der Daten auftritt.
     */
    public List<String> loadWorldData(String worldName) throws IOException {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("Welt " + worldName + " nicht gefunden.");
        }

        File worldFolder = world.getWorldFolder();
        File dataFile = new File(worldFolder, "world_data.txt");

        if (!dataFile.exists()) {
            throw new IOException("Datenfile für Welt " + worldName + " existiert nicht.");
        }

        return Files.readAllLines(Paths.get(dataFile.getPath()));
    }
}
