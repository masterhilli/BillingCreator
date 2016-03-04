package work.billing.Cache;

import work.billing.Spreadsheets.COL;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public abstract class BaseSpreadSheetMatrix {
    // TODO: change that back to private, ASAP we are finished here
    public AbstractMap<Integer, AbstractMap<Integer, String>> cellMatrix = new ConcurrentHashMap<>();

    protected void putValueToMatrixAt(COL column, int currentRow, String value) {
        Integer col = column.ordinal();
        if (this.cellMatrix.get(col) == null) {
            this.cellMatrix.put(col, new ConcurrentHashMap<>());
        }
        this.cellMatrix.get(col).put(currentRow, value);
    }
}

