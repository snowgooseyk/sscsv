package com.github.snowgooseyk.sscsv.base;

public class IndexedValue<T> implements Comparable<IndexedValue<T>> {

    private final int index;
    private final T value;

    public IndexedValue(int index, T value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public T getValue() {
        return value;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object other) {
        if (other instanceof IndexedValue) {
            return this.index == ((IndexedValue) other).index;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(IndexedValue<T> o) {
        if (o.index > index) {
            return -1;
        } else if (o.index < index) {
            return 1;
        }
        return 0;
    }
}
