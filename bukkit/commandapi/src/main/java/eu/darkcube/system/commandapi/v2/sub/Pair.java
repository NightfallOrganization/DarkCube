package eu.darkcube.system.commandapi.v2.sub;

public class Pair<T, V> {

	T v1;
	V v2;

	public Pair(T v1, V v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public T getFirst() {
		return v1;
	}
	
	public V getSecond() {
		return v2;
	}
}
