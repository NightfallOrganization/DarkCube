/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.equipment.crafting;

import eu.darkcube.system.libs.com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeBook {
    private static RecipeBook instance = new RecipeBook();
    private List<Recipe> recipes = new ArrayList<>();
    private static Gson gson = new Gson();

    public static RecipeBook getInstance() {
        return instance;
    }

    public void loadRecipies(Path path) throws IOException {
        if (Files.exists(path)) {

            var files = Files.walk(path);
            for (Iterator<Path> it = files.iterator(); it.hasNext(); ) {
                Path path1 = it.next();
                var reader = Files.newBufferedReader(path1);
                reader.readLine();//todo type check and then read the recipies
                var recipe = gson.fromJson(reader, BasicRecipe.class);
                recipes.add(recipe);
                
            }

        } else {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public List<Recipe> getRecipes() {
        return recipes;
    }


}
