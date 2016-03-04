package work.billing.Export;

import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;
import work.billing.Spreadsheets.SpreadsheetFormulas;

import java.util.List;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public class InternalProjectSummary extends BaseSpreadSheetMatrix{

    private final String prjLeadName;
    private int lastPos;
    private int firstPos;

    public int getLastPos() { return lastPos; }

    public InternalProjectSummary(String prjLeadName) {
        this.prjLeadName = prjLeadName;
    }

    public void initialize(int fromPos, int toPos) {
        int currentPos = firstPos = toPos+1;
        putLineForSummaryOfNetVatPreTax(currentPos++);
        currentPos++; // skip this row, because here we put the check in later!
        putLineForProjectLeadsCalculation(currentPos++, fromPos, toPos);
        putLineWithInfoRounded(currentPos++);
        putLineTeamHours(currentPos++, fromPos);
        putLineSumForBilling(currentPos++);
        putLinePrjLeadHourRate(currentPos++);
        lastPos = currentPos;
    }

    protected void putLineForSummaryOfNetVatPreTax(int row) {
        putValueToMatrixAt(COL.A, row, I18N.SUM_AMOUNT.toUpperCase());
        // Net
        putValueToMatrixAt(COL.D, row, SpreadsheetFormulas.SUM(COL.D, 1,row-1)+"/2");
        // VAT
        putValueToMatrixAt(COL.E, row, SpreadsheetFormulas.SUM(COL.E, 1,row-1)+"/2");
        // PRE tax
        putValueToMatrixAt(COL.F, row, SpreadsheetFormulas.SUM(COL.F, 1,row-1)+"/2");
    }

    protected void putLineForProjectLeadsCalculation(int row, int fromPos, int toPos) {
        putValueToMatrixAt(COL.B, row, prjLeadName);
        putValueToMatrixAt(COL.C, row, I18N.PRJ_LEAD_HOURS);
        putValueToMatrixAt(COL.D, row,
                SpreadsheetFormulas.SUMIF(COL.B.toString(), COL.A.toString(), fromPos, toPos,
                        COL.B.toString(), row));
        putValueToMatrixAt(COL.E, row, "50");
        putValueToMatrixAt(COL.G, row, SpreadsheetFormulas.TIMES(COL.D, row, COL.E, row));
    }

    protected void putLineWithInfoRounded(int row) {
        putValueToMatrixAt(COL.E, row, I18N.ROUNDED);
    }

    protected void putLineTeamHours(int currentPos, int fromPos) {
        putValueToMatrixAt(COL.C, currentPos, I18N.TEAM_HOURS);
        putValueToMatrixAt(COL.D, currentPos,
                String.format("=A%d-D%d", fromPos, currentPos - 2));
        putValueToMatrixAt(COL.E, currentPos,
                String.format("=IF(MOD(D%d,50)=0,D%d,(ROUNDDOWN(D%d/50,0)+1)*50)",
                        currentPos, currentPos, currentPos));
        putValueToMatrixAt(COL.F, currentPos, "4.00");
        putValueToMatrixAt(COL.G, currentPos,
                String.format("=MIN(E%d*F%d,G%d)", currentPos, currentPos, currentPos - 2));
    }

    private void putLineSumForBilling(int row) {
        putValueToMatrixAt(COL.C, row, I18N.BILLING_SUM);
        putValueToMatrixAt(COL.G, row, SpreadsheetFormulas.SUM(COL.G, row-3, row-1));
    }

    private void putLinePrjLeadHourRate(int row) {
        putValueToMatrixAt(COL.C, row, I18N.EFFECTIVE_HOUR_RATE);
        putValueToMatrixAt(COL.G, row, String.format("=G%d/D%d", row - 1, row - 4));
    }

    public void initializeFieldForOverallSummary(List<? extends ProjectPositions> prjPositions) {
        int checkerRow = firstPos +1;
        putValueToMatrixAt(COL.C, checkerRow,
                String.format("=if(D%d<>D%d,\"da stimmt was nicht\",\"Summen passen\")", checkerRow-1, checkerRow));
        String valueForSumOfProjects = getSummaryString(prjPositions);
        putValueToMatrixAt(COL.D, checkerRow, valueForSumOfProjects);

    }

    private String getSummaryString(List<? extends ProjectPositions> prjPositions) {
        String valueForSumOfProjects = "=";
        for (ProjectPositions prjRowRef : prjPositions) {
            if (valueForSumOfProjects.length() != 1) {
                valueForSumOfProjects += "+";
            }
            valueForSumOfProjects += String.format("C%d", prjRowRef.getSumRow());
        }
        return valueForSumOfProjects;
    }
}
