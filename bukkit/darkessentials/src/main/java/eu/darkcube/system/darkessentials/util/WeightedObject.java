package eu.darkcube.system.darkessentials.util;
public class WeightedObject {
	Object object;
	int weight;

	public WeightedObject(Object object, int weight) {
		this.object = object;
		this.weight = weight;
	}

	public Object getObject() {
		return object;
	}

	public int getWeight() {
		return weight;
	}
}
