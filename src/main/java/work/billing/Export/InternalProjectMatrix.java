package work.billing.Export;

import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;
import work.billing.Spreadsheets.SpreadsheetFormulas;
import work.billing.Timesheet.TrackedTime;

import java.util.List;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class InternalProjectMatrix extends BaseSpreadSheetMatrix implements ProjectPositions {

    private final String projectName;
    private final List<TrackedTime> times;
    private int sumRow = 0;
    private int headingRow = 0;
    public int getHeadingRow() { return headingRow; }
    public int getSumRow() { return sumRow; }
    public int getTravelCostRow() { return sumRow-1; }

    public InternalProjectMatrix(String projectName, List<TrackedTime> times) {
        this.projectName = projectName;
        this.times = times;
    }

    public void initializeMatrix(int currentPos ) {
        putHeadingToMatrix(currentPos++);
        int fromPos = currentPos;
        double sumOfTravelCosts = 0.0;
        for (TrackedTime timeTracked : times) {
            putTimeForTeamMemberToMatrix(currentPos++, timeTracked);
            sumOfTravelCosts += timeTracked.getTravelCosts();
        }
        putTravelCostsToMatrix(currentPos++, sumOfTravelCosts);
        putSumLineToMatrix(currentPos, fromPos);
    }

    protected void putHeadingToMatrix(int currentRow) {
        this.headingRow = currentRow;
        putValueToMatrixAt(COL.A, currentRow, projectName);
        putValueToMatrixAt(COL.C, currentRow, I18N.HOUR_RATE);
        putValueToMatrixAt(COL.D, currentRow, I18N.NET);
        putValueToMatrixAt(COL.E, currentRow, I18N.VAT);
        putValueToMatrixAt(COL.F, currentRow, I18N.PRE_TAX);
    }

    protected void putTimeForTeamMemberToMatrix(int currentRow, TrackedTime timetracked) {
        putValueToMatrixAt(COL.A, currentRow, SpreadsheetFormulas.formatDouble(timetracked.getHours()));
        putValueToMatrixAt(COL.B, currentRow, timetracked.getTeamMember());
        putValueToMatrixAt(COL.C, currentRow, Integer.toString(timetracked.getHourRate()));
        putValueToMatrixAt(COL.D, currentRow,
                SpreadsheetFormulas.TIMES(COL.A, currentRow, COL.C, currentRow));

        putVATtoMatrix(currentRow);
    }

    protected void putVATtoMatrix(int currentRow) {
        putValueToMatrixAt(COL.E, currentRow, SpreadsheetFormulas.PERCENT_OF(COL.D.toString(), currentRow, 0.2));
        putValueToMatrixAt(COL.F, currentRow,
                SpreadsheetFormulas.ADD(COL.D.toString(), currentRow, COL.E.toString(), currentRow));
    }

    protected void putTravelCostsToMatrix(int currentRow, double sumOfTravelCosts) {
        putValueToMatrixAt(COL.B, currentRow, I18N.TRAVEL_COSTS);
        putValueToMatrixAt(COL.D, currentRow, SpreadsheetFormulas.formatDouble(sumOfTravelCosts));

        putVATtoMatrix(currentRow);
    }

    protected void putSumLineToMatrix(int currentRow, int fromPos) {
        this.sumRow=currentRow;
        putValueToMatrixAt(COL.D, currentRow,
                SpreadsheetFormulas.SUM(COL.D, fromPos, currentRow-1 ));
        putValueToMatrixAt(COL.E, currentRow,
                SpreadsheetFormulas.SUM(COL.E, fromPos, currentRow-1));
        putValueToMatrixAt(COL.F, currentRow,
                SpreadsheetFormulas.SUM(COL.F, fromPos, currentRow-1));
    }
}
