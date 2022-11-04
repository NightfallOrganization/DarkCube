package eu.darkcube.system.cloudban.util.ban;

import com.google.gson.JsonDeserializer;

public abstract class JsonSerializer<T> implements com.google.gson.JsonSerializer<T>, JsonDeserializer<T>
{

}
