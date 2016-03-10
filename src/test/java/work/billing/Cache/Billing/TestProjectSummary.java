package work.billing.Cache.Billing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import work.billing.Cache.CacheBasic;
import work.billing.Cache.MockPrjPos;
import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhillbrand on 3/5/2016.
 */
public class TestProjectSummary extends CacheBasic{
    public static final int FIRST_ROW = 2;
    public static final int LAST_ROW = 8;
    private ProjectSummary prjSum;
    private static List<MockPrjPos> prjMatrices;

    @BeforeClass
    public static void setUpClass() {
        prjMatrices = new ArrayList<>();
        prjMatrices.add(new MockPrjPos(22));
        prjMatrices.add(new MockPrjPos(33));
        prjMatrices.add(new MockPrjPos(44));
        prjMatrices.add(new MockPrjPos(55));
    }
    @Before
    public void setUp() {
        prjSum = new ProjectSummary(prjMatrices, FIRST_ROW, LAST_ROW);
        super.setUp(12, prjSum);
    }

    @Test
    public void putHeadingToMatrix() {
        prjSum.putHeadingForShortSummaryOfBillingProject(getCurrentRow());

        assertMatrix(I18N.DESCRIPTION, COL.B, getCurrentRow());
        assertMatrix(I18N.NET, COL.C, getCurrentRow());
        assertMatrix(I18N.TAX_PERCENTAGE, COL.D, getCurrentRow());
        assertMatrix(I18N.VAT, COL.E, getCurrentRow());
        assertMatrix(I18N.PRE_TAX, COL.F, getCurrentRow());
    }

    @Test
    public void putProjectReferences() {
        int actual = prjSum.putProjectReferences(getCurrentRow());

        Assert.assertEquals(getCurrentRow()+4, actual);
    }

    @Test
    public void putProjectReferenceToMatrix() {
        prjSum.putProjectReferenceToMatrix(getCurrentRow(), prjMatrices.get(0));

        assertMatrix("=A18", COL.B, getCurrentRow());
        assertMatrix("=C22", COL.C, getCurrentRow());
        assertMatrix(I18N.PERCENT20, COL.D, getCurrentRow());
        assertMatrix("=E22", COL.E, getCurrentRow());
        assertMatrix("=F22", COL.F, getCurrentRow());
    }

    @Test
    public void putSummaryToMatrix() {
        prjSum.putSummaryToMatrix(getCurrentRow(), 4);
        assertMatrix(I18N.SUM_AMOUNT, COL.B, getCurrentRow());
        assertMatrix(String.format("=SUM(C4:C%d)", getCurrentRow()-1) , COL.C, getCurrentRow());
        assertMatrix(String.format("=SUM(E4:E%d)", getCurrentRow()-1) , COL.E, getCurrentRow());
        assertMatrix(String.format("=SUM(F4:F%d)", getCurrentRow()-1) , COL.F, getCurrentRow());
    }

    @Test
    public void putCheckIfValuesAreCorrect() {
        prjSum.putCheckIfValuesAreCorrect(getCurrentRow());
        assertMatrix(
                String.format("=if(C%d<>SUM(D%d:D%d)/2,\"Betrag nicht gleich\",\"passt\")", getCurrentRow(), FIRST_ROW, LAST_ROW),
                COL.G, getCurrentRow());
    }

    @Test
    public void initializeProjectSummary_Returns30cells() {
        prjSum.initialize(getCurrentRow());

        int actual = 0;
        for (COL col : prjSum.cellMatrix.keySet()) {
            actual += prjSum.cellMatrix.get(col).keySet().size();
        }

        Assert.assertEquals(1, prjSum.cellMatrix.get(COL.G).size());
        Assert.assertEquals(30, actual);
    }
}
