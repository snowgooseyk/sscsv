package com.github.snowgooseyk.sscsv.base;

import java.util.List;

public class Row {

    private final List<?> columns;
    private final int rowNumber;

    public Row(int rowNumber, List<?> columns) {
        this.columns = columns;
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getRawColumnValues() {
        return (List<T>) columns;
    }
}
