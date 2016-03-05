package work.billing.Cache;

import org.junit.Assert;
import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;

/**
 * Created by mhillbrand on 3/5/2016.
 */
public class CacheBasic {
    private int currentRow;
    private BaseSpreadSheetMatrix matrix;

    protected int getCurrentRow() {
        return currentRow;
    }

    public void setUp(int currentRow, BaseSpreadSheetMatrix matrix) {
        this.currentRow = currentRow;
        this.matrix = matrix;
    }

    protected void assertMatrix(String expected, COL col, int row) {
        Assert.assertEquals(expected, matrix.cellMatrix.get(col.ordinal()).get(row));
    }

    protected void assertReferences(int intPrjRow) {
        assertMatrix(String.format("=ROUND(D%d,2)", intPrjRow), COL.C, currentRow);
        assertMatrix(I18N.PERCENT20, COL.D, currentRow);
        assertMatrix(String.format("=ROUND(E%d,2)", intPrjRow), COL.E, currentRow);
        assertMatrix(String.format("=ROUND(F%d,2)", intPrjRow), COL.F, currentRow);
    }
}
