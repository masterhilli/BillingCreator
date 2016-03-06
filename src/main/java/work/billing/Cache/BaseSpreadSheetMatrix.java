package work.billing.Cache;

import work.billing.Spreadsheets.COL;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public abstract class BaseSpreadSheetMatrix {
    // TODO: change that back to private, ASAP we are finished here
    public AbstractMap<COL, AbstractMap<Integer, String>> cellMatrix = new ConcurrentHashMap<>();

    protected void putValueToMatrixAt(COL column, int currentRow, String value) {
        if (this.cellMatrix.get(column) == null) {
            this.cellMatrix.put(column, new ConcurrentHashMap<>());
        }
        this.cellMatrix.get(column).put(currentRow, value);
    }
}

