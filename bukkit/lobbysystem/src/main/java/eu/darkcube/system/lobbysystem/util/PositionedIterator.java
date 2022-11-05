package eu.darkcube.system.lobbysystem.util;

import java.util.Arrays;

public class PositionedIterator<T> {

	private final T[] array;
	private final int max;

	private volatile int position = 0;

	public PositionedIterator(T[] array) {
		this.array = Arrays.copyOf(array, array.length);
		this.max = this.array.length;
	}

	public T[] elements(final int position) {
		final T[] list = Arrays.copyOf(this.array, this.array.length);
		for (int i = 0; i < this.array.length; i++) {
			list[i] = this.array[(position + i) % getMax()];
		}
		return list;
	}

	public T[] elements() {
		return this.elements(this.position());
	}

	public T element(final int position) {
		return this.array[position];
	}

	public T element() {
		return this.element(this.position);
	}

	public void next() {
		this.position(this.position() + 1);
	}

	public void previous() {
		this.position(this.position() - 1);
	}

	public int position() {
		return this.position;
	}

	public void position(final int position) {
		this.position = position % getMax();
		while (this.position < 0) {
			this.position += getMax();
		}
	}

	public int getMax() {
		return this.max;
	}
}
