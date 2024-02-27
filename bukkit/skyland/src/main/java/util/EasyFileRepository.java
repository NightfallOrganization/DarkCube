/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package util;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.skyland.worldgen.structures.SkylandStructure;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class EasyFileRepository<T> {

    String path;
    Class<T> clazz;
    Gson gson = new Gson();

    public EasyFileRepository(String path, Class<T> clazz) {
        this.path = path;
        this.clazz = clazz;

        var pathSegments = path.split("/");
        String previousPath = "";
        for (String pathSegment : pathSegments) {
            File file = new File(pathSegment);
            if (!file.exists()) {
                file.mkdirs();
            }
            previousPath = previousPath + "/" + pathSegment;
        }
    }

    public T loadFromFile(String fileName) {
        File file = new File(path + "/" + fileName);

        if (!file.exists()) {
            System.out.println("no File found with file name: " + fileName);
        }



        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            fileInputStream.close();
            return gson.fromJson(content, clazz);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void saveToFile(String fileName, T content){
        File file = new File(path + "/" + fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String output = gson.toJson(content);
            Files.writeString(file.toPath(), output, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
