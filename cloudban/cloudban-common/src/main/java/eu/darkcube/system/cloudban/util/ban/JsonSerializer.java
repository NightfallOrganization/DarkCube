/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.ban;

import com.google.gson.JsonDeserializer;

public abstract class JsonSerializer<T> implements com.google.gson.JsonSerializer<T>, JsonDeserializer<T>
{

}
