package work.billing.Export;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public abstract class BaseSpreadSheetMatrix {
    protected enum COL {
        NIL (0, "_"),
        A (1, "A"),
        B (2, "B"),
        C (3, "C"),
        D (4, "D"),
        E (5, "E"),
        F (6, "F"),
        G (7, "G");

        COL (int val, String colName) {
            this.val = val;
            this.colName = colName;
        }
        private final int val;
        private final String colName;

        @Override
        public String toString() { return this.colName; }
    }


    // TODO: change that back to private, ASAP we are finished here
    public AbstractMap<Integer, AbstractMap<Integer, String>> cellMatrix = new ConcurrentHashMap<>();

    protected void putValueToMatrixAt(int column, int currentRow, String value) {
        Integer col = column;
        if (this.cellMatrix.get(col) == null) {
            this.cellMatrix.put(col, new ConcurrentHashMap<>());
        }
        this.cellMatrix.get(col).put(currentRow, value);
    }
}

