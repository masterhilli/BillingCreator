package work.billing.Cache.Internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import work.billing.Cache.ProjectPositions;
import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mhillbrand on 3/5/2016.
 */
public class TestProjectSummary {
    private ProjectSummary prjSum;
    private static List<MockPrjPos> projectPositions;
    private int row;
    private final String PRJ_LEAD = "Test";
    private final int rowTeamMemberSums = 10;

    @BeforeClass
    public static void setUpClass() {
        projectPositions = new ArrayList<>();
        projectPositions.add(new MockPrjPos(11));
        projectPositions.add(new MockPrjPos(22));
        projectPositions.add(new MockPrjPos(33));
    }

    @Before
    public void setUp() {
        prjSum = new ProjectSummary(PRJ_LEAD, rowTeamMemberSums);
        row = 11;
    }
    @Test
    public void putLineForSummaryOfNetVatPreTax_WithRow11AndTeamMemberRow10_ReturnsSumsFor1to10() {
        prjSum.putLineForSummaryOfNetVatPreTax(row);

        Assert.assertEquals(I18N.SUM_AMOUNT.toUpperCase(), prjSum.cellMatrix.get(COL.A.ordinal()).get(row));
        Assert.assertEquals("=SUM(D1:D10)/2", prjSum.cellMatrix.get(COL.D.ordinal()).get(row));
        Assert.assertEquals("=SUM(E1:E10)/2", prjSum.cellMatrix.get(COL.E.ordinal()).get(row));
        Assert.assertEquals("=SUM(F1:F10)/2", prjSum.cellMatrix.get(COL.F.ordinal()).get(row));
    }

    @Test
    public void putLineForProjectLeadsCalculation_WithTestLead_ReturnsPrjLeadLine() {
        prjSum.putLineForProjectLeadsCalculation(row, 5, 7);

        Assert.assertEquals(PRJ_LEAD, prjSum.cellMatrix.get(COL.B.ordinal()).get(row));
        Assert.assertEquals(I18N.PRJ_LEAD_HOURS, prjSum.cellMatrix.get(COL.C.ordinal()).get(row));
        Assert.assertEquals("=SUMIF($B$5:$B$7,B11,$A$5:$A$7)", prjSum.cellMatrix.get(COL.D.ordinal()).get(row));
        Assert.assertEquals("50", prjSum.cellMatrix.get(COL.E.ordinal()).get(row));
        Assert.assertEquals("=D11*E11", prjSum.cellMatrix.get(COL.G.ordinal()).get(row));
    }

    @Test
    public void putLineWithInfoRounded_WithRow11_ReturnsRoundedAtColERow11(){
        prjSum.putLineWithInfoRounded(row);

        Assert.assertEquals(I18N.ROUNDED, prjSum.cellMatrix.get(COL.E.ordinal()).get(row));
    }

    @Test
    public void putLineTeamHours_WithRow11_ReturnsTeamFormulasAtRow11() {
        prjSum.putLineTeamHours(row);

        Assert.assertEquals(I18N.TEAM_HOURS, prjSum.cellMatrix.get(COL.C.ordinal()).get(row));
        Assert.assertEquals("=A10-D9", prjSum.cellMatrix.get(COL.D.ordinal()).get(row));
        Assert.assertEquals("=IF(MOD(D11,50)=0,D11,(ROUNDDOWN(D11/50,0)+1)*50)", prjSum.cellMatrix.get(COL.E.ordinal()).get(row));
        Assert.assertEquals("4.00", prjSum.cellMatrix.get(COL.F.ordinal()).get(row));
        Assert.assertEquals("=MIN(E11*F11,G9)", prjSum.cellMatrix.get(COL.G.ordinal()).get(row));
    }

    @Test
    public void putLineSumForBilling_WithRow11_ReturnsSumFormulaAtColGRow11() {
        prjSum.putLineSumForBilling(row);

        Assert.assertEquals(I18N.BILLING_SUM, prjSum.cellMatrix.get(COL.C.ordinal()).get(row));
        Assert.assertEquals("=SUM(G8:G10)", prjSum.cellMatrix.get(COL.G.ordinal()).get(row));
    }

    @Test
    public void putLinePrjLeadHourRate_WithRow11_ReturnsSumFormulaAtColGRow11() {
        prjSum.putLinePrjLeadHourRate(row);

        Assert.assertEquals(I18N.EFFECTIVE_HOUR_RATE, prjSum.cellMatrix.get(COL.C.ordinal()).get(row));
        Assert.assertEquals("=G10/D7", prjSum.cellMatrix.get(COL.G.ordinal()).get(row));
    }

    @Test
    public void initializeFieldForOverallSummary_WithRow11_ReturnsAddFormula() {
        prjSum.initialize(1, rowTeamMemberSums);

        prjSum.initializeFieldForOverallSummary(projectPositions);

        Assert.assertEquals("=if(D11<>D12,\"da stimmt was nicht\",\"Summen passen\")", prjSum.cellMatrix.get(COL.C.ordinal()).get(row+1));
        Assert.assertEquals("=C11+C22+C33", prjSum.cellMatrix.get(COL.D.ordinal()).get(row+1));
    }

    @Test
    public void initialize_WithFromPos1AndToPos10_ReturnsInCellMatrixWithXCells() {
        prjSum.initialize(1, rowTeamMemberSums);

        int cellCount = 0;
        for (Integer col: prjSum.cellMatrix.keySet()) {
            cellCount += prjSum.cellMatrix.get(col).size();
        }

        Assert.assertEquals(19, cellCount);
    }

    private static class MockPrjPos implements ProjectPositions {
        private final int sumRow;

        public MockPrjPos(int sumRow) {
            this.sumRow = sumRow;
        }
        @Override
        public int getHeadingRow() {
            return sumRow-4;
        }

        @Override
        public int getSumRow() {
            return sumRow;
        }

        @Override
        public int getTravelCostRow() {
            return sumRow-1;
        }
    }
}
