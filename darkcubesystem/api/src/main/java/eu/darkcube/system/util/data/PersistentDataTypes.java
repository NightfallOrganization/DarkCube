/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public class PersistentDataTypes {
	public static final PersistentDataType<BigInteger> BIGINTEGER =
			new PersistentDataType<BigInteger>() {
				@Override
				public BigInteger deserialize(JsonDocument doc, String key) {
					return doc.getBigInteger(key);
				}

				@Override
				public void serialize(JsonDocument doc, String key, BigInteger data) {
					doc.append(key, data);
				}

				@Override
				public BigInteger clone(BigInteger object) {
					return object;
				}
			};
	public static final PersistentDataType<JsonDocument> JSONDOCUMENT =
			new PersistentDataType<JsonDocument>() {
				@Override
				public JsonDocument deserialize(JsonDocument doc, String key) {
					return doc.getDocument(key);
				}

				@Override
				public void serialize(JsonDocument doc, String key, JsonDocument data) {
					doc.append(key, data);
				}

				@Override
				public JsonDocument clone(JsonDocument object) {
					return object.clone();
				}
			};
	public static final PersistentDataType<String> STRING = new PersistentDataType<String>() {
		@Override
		public String deserialize(JsonDocument doc, String key) {
			return doc.getString(key);
		}

		@Override
		public void serialize(JsonDocument doc, String key, String data) {
			doc.append(key, data);
		}

		@Override
		public String clone(String object) {
			return object;
		}
	};
	public static final PersistentDataType<byte[]> BYTE_ARRAY = new PersistentDataType<byte[]>() {
		@Override
		public byte[] deserialize(JsonDocument doc, String key) {
			return doc.getBinary(key);
		}

		@Override
		public void serialize(JsonDocument doc, String key, byte[] data) {
			doc.append(key, data);
		}

		@Override
		public byte[] clone(byte[] object) {
			return object.clone();
		}
	};
	public static final PersistentDataType<Boolean> BOOLEAN = new PersistentDataType<Boolean>() {
		@Override
		public Boolean deserialize(JsonDocument doc, String key) {
			return doc.getBoolean(key);
		}

		@Override
		public void serialize(JsonDocument doc, String key, Boolean data) {
			doc.append(key, data);
		}

		@Override
		public Boolean clone(Boolean object) {
			return object;
		}
	};
	public static final PersistentDataType<Integer> INTEGER = new PersistentDataType<Integer>() {

		@Override
		public Integer deserialize(JsonDocument doc, String key) {
			return doc.getInt(key);
		}

		@Override
		public void serialize(JsonDocument doc, String key, Integer data) {
			doc.append(key, data);
		}

		@Override
		public Integer clone(Integer object) {
			return object;
		}
	};
	public static final PersistentDataType<Long> LONG = new PersistentDataType<Long>() {
		@Override
		public Long deserialize(JsonDocument doc, String key) {
			return doc.getLong(key);
		}

		@Override
		public void serialize(JsonDocument doc, String key, Long data) {
			doc.append(key, data);
		}

		@Override
		public Long clone(Long object) {
			return object;
		}
	};
	public static final PersistentDataType<Double> DOUBLE = new PersistentDataType<Double>() {
		@Override
		public Double deserialize(JsonDocument doc, String key) {
			return doc.getDouble(key);
		}

		@Override
		public void serialize(JsonDocument doc, String key, Double data) {
			doc.append(key, data);
		}

		@Override
		public Double clone(Double object) {
			return object;
		}
	};
	public static final PersistentDataType<Location> LOCATION = new PersistentDataType<Location>() {

		@Override
		public Location deserialize(JsonDocument doc, String key) {
			JsonDocument d = doc.getDocument(key);
			double x = d.getDouble("x");
			double y = d.getDouble("y");
			double z = d.getDouble("z");
			float yaw = d.getFloat("yaw");
			float pitch = d.getFloat("pitch");
			World world = Bukkit.getWorld(UUID.fromString(d.getString("world")));
			return new Location(world, x, y, z, yaw, pitch);
		}

		@Override
		public void serialize(JsonDocument doc, String key, Location data) {
			JsonDocument d = new JsonDocument();
			d.append("x", data.getX());
			d.append("y", data.getY());
			d.append("z", data.getZ());
			d.append("yaw", data.getYaw());
			d.append("pitch", data.getPitch());
			d.append("world", data.getWorld().getUID().toString());
			doc.append(key, d);
		}

		@Override
		public Location clone(Location object) {
			return object.clone();
		}
	};

	public static <T> PersistentDataType<Set<T>> set(PersistentDataType<T> dataType) {
		PersistentDataType<List<T>> l = list(dataType);
		return new PersistentDataType<Set<T>>() {
			@Override
			public Set<T> deserialize(JsonDocument doc, String key) {
				return new HashSet<>(l.deserialize(doc, key));
			}

			@Override
			public void serialize(JsonDocument doc, String key, Set<T> data) {
				l.serialize(doc, key, new ArrayList<>(data));
			}

			@Override
			public Set<T> clone(Set<T> object) {
				Set<T> set = new HashSet<>();
				for (T value : object) {
					set.add(dataType.clone(value));
				}
				return set;
			}
		};
	}

	public static <T> PersistentDataType<List<T>> list(PersistentDataType<T> dataType) {
		return new PersistentDataType<List<T>>() {
			@Override
			public List<T> deserialize(JsonDocument doc, String key) {
				List<T> l = new ArrayList<>();
				int i = 0;
				JsonDocument d = doc.getDocument(key);
				while (d.contains(Integer.toString(i))) {
					l.add(dataType.deserialize(d, Integer.toString(i++)));
				}
				return l;
			}

			@Override
			public void serialize(JsonDocument doc, String key, List<T> data) {
				JsonDocument d = new JsonDocument();
				int i = 0;
				for (T t : data) {
					dataType.serialize(d, Integer.toString(i++), t);
				}
				doc.append(key, d);
			}

			@Override
			public List<T> clone(List<T> object) {
				List<T> list = new ArrayList<>();
				for (T value : object) {
					list.add(dataType.clone(value));
				}
				return list;
			}
		};
	}

	public static <T extends Enum<T>> PersistentDataType<T> enumType(Class<T> cls) {
		return new PersistentDataType<T>() {
			@Override
			public T deserialize(JsonDocument doc, String key) {
				return doc.get(key, cls);
			}

			@Override
			public void serialize(JsonDocument doc, String key, T data) {
				doc.append(key, data);
			}

			@Override
			public T clone(T object) {
				return object;
			}
		};
	}

	public static <T, V> PersistentDataType<T> map(PersistentDataType<V> type,
			Function<T, V> serialize, Function<V, T> deserialize, Function<T, T> clone) {
		return new PersistentDataType<T>() {
			@Override
			public T deserialize(JsonDocument doc, String key) {
				return deserialize.apply(type.deserialize(doc, key));
			}

			@Override
			public void serialize(JsonDocument doc, String key, T data) {
				type.serialize(doc, key, serialize.apply(data));
			}

			@Override
			public T clone(T object) {
				return clone.apply(object);
			}
		};
	}

}
