package com.github.snowgooseyk.sscsv.base.dsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.snowgooseyk.sscsv.base.Row;
import com.github.snowgooseyk.sscsv.base.Utils;

public class DelimitedReadIterator implements Iterator<Row> {

    private final char delimitor;
    private int rownum = 0;
    private String currentLine = null;
    private final BufferedReader currentReader;
    private static final String QUOT = "\"";
    private boolean autoClose = true;

    public DelimitedReadIterator(char delimitor, BufferedReader resource, boolean autoClose) {
        this.delimitor = delimitor;
        this.currentReader = resource;
        this.autoClose = autoClose;
    }

    @Override
    public boolean hasNext() {
        proceedNextLine(true);
        final boolean eof = (getCurrentLine() == null);
        if (eof && autoClose()) {
            close();
        }
        return eof == false;
    }

    @Override
    public Row next() {
        proceedNextLine(true);
        final List<String> result = new ArrayList<String>();
        final char delimitor = getDelimitor();
        List<String> splitted = split(getCurrentLine(), delimitor);
        Iterator<String> splittedValues = splitted.iterator();
        boolean straddleRow = false;
        while (splittedValues.hasNext()) {
            final String rawValue = splittedValues.next();
            final StringBuilder buffer = new StringBuilder(rawValue);
            while (notValancedQuote(buffer)) {
                if (splittedValues.hasNext()) {
                    if (!straddleRow) {
                        buffer.append(delimitor);
                    } else {
                        straddleRow = false;
                    }
                    buffer.append(splittedValues.next());
                } else {
                    clearCurrentLine();
                    proceedNextLine(false);
                    final String currentLine = getCurrentLine();
                    if (currentLine == null) {
                        // reach EOF.
                        break;
                    }
                    splitted = split(currentLine, delimitor);
                    splittedValues = splitted.iterator();
                    straddleRow = true;
                }
            }
            result.add(removeQuote(buffer));
        }
        clearCurrentLine();
        return new Row(getRownum(), result);
    }

    protected final void clearCurrentLine() {
        this.currentLine = null;
    }

    protected void proceedNextLine(boolean enableProceedRownum) {
        if (getCurrentLine() == null) {
            try {
                scrollCurrentLine();
            } catch (final IOException e) {
            	// TODO
                throw new RuntimeException(e);
            }
            if (enableProceedRownum) {
                proceedRownum();
            }
        }
    }

    protected final void scrollCurrentLine() throws IOException {
        currentLine = getReader().readLine();
    }

    protected String removeQuote(StringBuilder buffer) {
        String result = buffer.toString();
        final String quot = getQuot();
        result = Utils.removeStart(result, quot);
        result = Utils.removeEnd(result, quot);
        result = Utils.replace(result, quot + quot, quot, -1);
        return result;
    }

    protected boolean notValancedQuote(StringBuilder buffer) {
        final int length = buffer.length();
        final String quot = getQuot();
        if (buffer.indexOf(quot) == 0 && (length == 1 || lastQuots(buffer) % 2 != 0)) {
            return true;
        }
        return false;
    }

    private int lastQuots(StringBuilder buffer) {
        final String buf = buffer.toString();
        int index = buf.length();
        int count = 1;
        final char quot = getQuot().charAt(0);
        while (--index > 0) {
            if (buf.charAt(index) != quot) {
                break;
            }
            count++;
        }
        return count;
    }

    public void close() {
        try {
            getReader().close();
        } catch (final IOException e) {
        	//TODO
            throw new RuntimeException(e);
        }
    }

    protected BufferedReader getReader() {
        return this.currentReader;
    }

    protected List<String> split(String target, char splitter) {
        final List<String> result = new ArrayList<String>();
        if (Utils.empty(target)) {
            return result;
        }
        final int length = target.length();
        int lastIndex = 0;
        int position = 0;
        boolean matches = false;
        while (position < length) {
            if (target.charAt(position) == splitter) {
                if (matches) {
                    if (lastIndex == position) {
                        result.add(Utils.EMPTY_STRING);
                    }
                    lastIndex = ++position;
                    continue;
                }
                final String tg = target.substring(lastIndex, position);
                // unicode byte order mark.(BOM)
                if (tg.isEmpty() || tg.codePointAt(0) == 65279) {
                    result.add(Utils.EMPTY_STRING);
                } else {
                    result.add(tg);
                }
                lastIndex = ++position;
                matches = true;
                continue;
            }
            position++;
            matches = false;
        }
        result.add(target.substring(lastIndex, position));
        return result;
    }

    protected char getDelimitor() {
        return delimitor;
    }

    protected String getQuot() {
        return QUOT;
    }

    protected int getRownum() {
        return rownum;
    }

    protected final void proceedRownum() {
        rownum = getRownum() + 1;
    }

    protected String getCurrentLine() {
        return currentLine;
    }

    protected boolean autoClose(){
    	return autoClose;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
