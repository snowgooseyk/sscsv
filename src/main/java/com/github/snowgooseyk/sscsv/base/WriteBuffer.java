package com.github.snowgooseyk.sscsv.base;

import java.io.Closeable;
import java.io.Flushable;

public interface WriteBuffer extends Closeable, Flushable {

	WriteBuffer append(int index, byte[] bites);

	WriteBuffer append(int index, char[] charactors);

	WriteBuffer append(int index, String value);

	WriteBuffer scroll();

    int getCurrentIndex();
}
