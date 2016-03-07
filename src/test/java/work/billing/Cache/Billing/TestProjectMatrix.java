package work.billing.Cache.Billing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import work.billing.Cache.CacheBasic;
import work.billing.Cache.MockPrjPos;
import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;

/**
 * Created by mhillbrand on 3/5/2016.
 */
public class TestProjectMatrix extends CacheBasic{
    private ProjectMatrix prjMatrix;
    private int intPrjReferenceSum = 12;

    @Before
    public void setUp () {
        prjMatrix = new ProjectMatrix(new MockPrjPos(12));
        setUp(30, prjMatrix);
    }

    @Test
    public void putHeadingToMatrix_WithPrjRefAtSum12_Returns2Rows() {
        prjMatrix.putHeadingToMatrix(getCurrentRow());

        assertMatrix("=A8", COL.A, getCurrentRow());
        assertMatrix(I18N.HOUR_SUM, COL.A, getCurrentRow()+1);
        assertMatrix(I18N.DESCRIPTION, COL.B, getCurrentRow()+1);
        assertMatrix(I18N.HOUR_RATE, COL.C, getCurrentRow()+1);
        assertMatrix(I18N.TAX_PERCENTAGE, COL.D, getCurrentRow()+1);
        assertMatrix(I18N.VAT, COL.E, getCurrentRow()+1);
        assertMatrix(I18N.PRE_TAX, COL.F, getCurrentRow()+1);
    }

    @Test
    public void putUsersToMatrix() {
        int actual = prjMatrix.putUsersToMatrix(getCurrentRow());

        Assert.assertEquals(getCurrentRow()+2, actual);
    }

    @Test
    public void putUserBillingInfoToMatrix() {
        prjMatrix.putUserBillingInfoToMatrix(getCurrentRow(), 5);

        assertMatrix("=A5",COL.A, getCurrentRow());
        assertMatrix("=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C5,\" EUR (\",B5,\")\")", COL.B, getCurrentRow());
        assertReferences(5);
    }

    @Test
    public void putTravelCostsToMatrix() {
        prjMatrix.putTravelCostsToMatrix(getCurrentRow());

        assertMatrix(I18N.TRAVEL_COSTS,COL.B, getCurrentRow());
        assertReferences(intPrjReferenceSum-1);
    }

    @Test
    public void putReferencesToInternalRowsToMatrix() {
        prjMatrix.putReferencesToInternalRowsToMatrix(getCurrentRow(),7);
        assertReferences(7);
    }

    @Test
    public void initializeMatrix() {
        prjMatrix.initialize(getCurrentRow());

        Assert.assertEquals(getCurrentRow()+5, prjMatrix.getSumRow());
        Assert.assertEquals(getCurrentRow(), prjMatrix.getHeadingRow());
        Assert.assertEquals(getCurrentRow()+4, prjMatrix.getTravelCostRow());
    }
}
