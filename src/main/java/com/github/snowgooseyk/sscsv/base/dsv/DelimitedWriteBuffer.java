package com.github.snowgooseyk.sscsv.base.dsv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.github.snowgooseyk.sscsv.base.IndexedValue;
import com.github.snowgooseyk.sscsv.base.WriteBuffer;

public class DelimitedWriteBuffer implements WriteBuffer {

    private final String charsetName;
    private final char delimitor;
    private final boolean requiredQuote;
    private final Writer writer;
    private final List<IndexedValue<String>> currentRow = new ArrayList<IndexedValue<String>>();
    private StringBuilder buffer = new StringBuilder();
    private int currentIndex = 1;

    public DelimitedWriteBuffer(String charsetName, char delimitor, OutputStream writer)
            throws IOException {
        this(charsetName, delimitor, true, writer);
    }

    public DelimitedWriteBuffer(String charsetName, char delimitor, boolean requiredQuote,
            OutputStream writer) throws IOException {
        super();
        this.charsetName = charsetName;
        this.delimitor = delimitor;
        this.requiredQuote = requiredQuote;
        this.writer = new OutputStreamWriter(writer, charsetName);
    }

    @Override
    public WriteBuffer append(int index, byte[] bites) {
        try {
            return append(index, new String(bites, charsetName));
        } catch (final UnsupportedEncodingException e) {
            // already checked at constractor.
        	//TODO
            throw new RuntimeException(e);
        }
    }

    @Override
    public WriteBuffer append(int index, char[] charactors) {
        return append(index, String.valueOf(charactors));
    }

    @Override
    public WriteBuffer append(int index, String str) {
        currentRow.add(new IndexedValue<String>(index, coverQuot(str)));
        return this;
    }

    protected String coverQuot(String str) {
        if (!requiredQuote) {
            if (StringUtils.isEmpty(str)) {
                return StringUtils.EMPTY;
            } else if (!StringUtils.containsAny(str, new char[] { delimitor, '"', '\r', '\n' })) {
                return str;
            }
        }
        if (StringUtils.isEmpty(str)) {
            return "\"\"";
        }
        final StringBuilder buffer = new StringBuilder(str.length() + 2);
        buffer.append('"').append(escapeQuot(str)).append('"');
        return buffer.toString();
    }

    private String escapeQuot(String str) {
        if (str.indexOf('"') >= 0) {
            str = str.replace("\"", "\"\"");
        }
        return str;
    }

    @Override
    public void flush() throws IOException {
        writer.write(this.buffer.toString());
        writer.flush();
        this.buffer = new StringBuilder();
    }

    @Override
    public WriteBuffer scroll() {
        if (!currentRow.isEmpty()) {
            Collections.sort(currentRow);
            int subappend = 0;
            for (int i = 0, limit = currentRow.size(); i < limit; i++) {
                final IndexedValue<String> value = currentRow.get(i);
                final int sub = value.getIndex() - (i + subappend + 1);
                if (sub != 0) {
                    for (int j = 0; j < sub; j++) {
                        this.buffer.append(coverQuot("")).append(delimitor);
                        subappend++;
                    }
                }
                this.buffer.append(value).append(delimitor);
            }
            buffer.deleteCharAt(this.buffer.length() - 1);
        }
        buffer.append(SystemUtils.LINE_SEPARATOR);
        this.currentIndex++;
        this.currentRow.clear();
        return this;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public int getCurrentIndex() {
        return this.currentIndex;
    }
}
