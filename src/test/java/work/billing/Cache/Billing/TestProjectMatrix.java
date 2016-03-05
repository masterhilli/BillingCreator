package work.billing.Cache.Billing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import work.billing.Cache.MockPrjPos;
import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;

/**
 * Created by mhillbrand on 3/5/2016.
 */
public class TestProjectMatrix {
    private ProjectMatrix prjMatrix;
    private int currentRow;
    private int intPrjReferenceSum = 12;

    @Before
    public void setUp () {
        prjMatrix = new ProjectMatrix(new MockPrjPos(12));
        currentRow = 30;
    }

    @Test
    public void putHeadingToMatrix_WithPrjRefAtSum12_Returns2Rows() {
        prjMatrix.putHeadingToMatrix(currentRow);

        assertMatrix("=A8", COL.A, currentRow);
        assertMatrix(I18N.HOUR_SUM, COL.A, currentRow+1);
        assertMatrix(I18N.DESCRIPTION, COL.B, currentRow+1);
        assertMatrix(I18N.HOUR_RATE, COL.C, currentRow+1);
        assertMatrix(I18N.TAX_PERCENTAGE, COL.D, currentRow+1);
        assertMatrix(I18N.VAT, COL.E, currentRow+1);
        assertMatrix(I18N.PRE_TAX, COL.F, currentRow+1);
    }

    @Test
    public void putUsersToMatrix() {
        int actual = prjMatrix.putUsersToMatrix(currentRow);

        Assert.assertEquals(currentRow+2, actual);
    }

    @Test
    public void putUserBillingInfoToMatrix() {
        prjMatrix.putUserBillingInfoToMatrix(currentRow, 5);

        assertMatrix("=A5",COL.A, currentRow);
        assertMatrix("=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C5,\" EUR (\",B5,\")\")", COL.B, currentRow);
        assertReferences(5);
    }

    @Test
    public void putTravelCostsToMatrix() {
        prjMatrix.putTravelCostsToMatrix(currentRow);

        assertMatrix(I18N.TRAVEL_COSTS,COL.B, currentRow);
        assertReferences(intPrjReferenceSum-1);
    }

    @Test
    public void putReferencesToInternalRowsToMatrix() {
        prjMatrix.putReferencesToInternalRowsToMatrix(currentRow,7);
        assertReferences(7);
    }

    private void assertMatrix(String expected, COL col, int row) {
        Assert.assertEquals(expected, prjMatrix.cellMatrix.get(col.ordinal()).get(row));
    }

    private void assertReferences(int intPrjRow) {
        assertMatrix(String.format("=ROUND(D%d,2)", intPrjRow), COL.C, currentRow);
        assertMatrix(I18N.PERCENT20, COL.D, currentRow);
        assertMatrix(String.format("=ROUND(E%d,2)", intPrjRow), COL.E, currentRow);
        assertMatrix(String.format("=ROUND(F%d,2)", intPrjRow), COL.F, currentRow);
    }
}
