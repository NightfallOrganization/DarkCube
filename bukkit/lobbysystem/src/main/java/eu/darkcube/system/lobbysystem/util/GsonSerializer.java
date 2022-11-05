package eu.darkcube.system.lobbysystem.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSerializer {

	public static final Gson gson;
	static {
		GsonBuilder b = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();
		b.addSerializationExclusionStrategy(new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				if (f.getAnnotation(DontSerialize.class) != null) {
					return true;
				}
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> var1) {
				if (var1.isAnnotationPresent(DontSerialize.class)) {
					return true;
				}
				return false;
			}
		});
		gson = b.create();
//		JsonDocument.GSON = gson;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface DontSerialize {

	}
}
