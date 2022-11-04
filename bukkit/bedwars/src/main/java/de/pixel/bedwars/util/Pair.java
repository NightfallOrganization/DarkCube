package de.pixel.bedwars.util;

public class Pair<T, U> {
	
	private T key;
	private U value;
	
	public Pair(T key, U value) {
		this.key = key;
		this.value = value;
	}

	public T getKey() {
		return key;
	}
	
	public U getValue() {
		return value;
	}
}
