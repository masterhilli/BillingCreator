package work.billing.Export;

import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;
import work.billing.Spreadsheets.SpreadsheetFormulas;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public class BillingProjectMatrix extends BaseSpreadSheetMatrix implements ProjectPositions{
    private final ProjectPositions internalPrjPosition;
    private int sumRow = 0;
    private int headingRow = 0;

    @Override
    public int getHeadingRow() { return headingRow; }

    @Override
    public int getSumRow() { return sumRow; }

    @Override
    public int getTravelCostRow() { return sumRow-1; }

    public BillingProjectMatrix(ProjectPositions internalPrjPosition) {
        this.internalPrjPosition = internalPrjPosition;
    }

    public void initialize(int startRow) {
        putHeadingToMatrix(startRow++);
        startRow++;
        startRow = putUsersToMatrix(startRow);
        putTravelCostsToMatrix(startRow++);
        putSumLineToMatrix(startRow, headingRow);
        putValueCheckerToMatrix(startRow);
    }

    private int putUsersToMatrix(int currentRow) {
        int internalPrjPersonCount = internalPrjPosition.getTravelCostRow()-internalPrjPosition.getHeadingRow()-1;
        for (int relativePosFromHeading = 1; relativePosFromHeading <= internalPrjPersonCount; relativePosFromHeading++) {
            putUserBillingInfoToMatrix(currentRow++, internalPrjPosition.getHeadingRow() + relativePosFromHeading);
        }
        return currentRow;
    }

    private void putUserBillingInfoToMatrix(int currentRow, int internalPrjTeamMemberRow) {
        putValueToMatrixAt(COL.A, currentRow, String.format("=A%d", internalPrjTeamMemberRow));
        putValueToMatrixAt(COL.B, currentRow,
                String.format("=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C%d,\" EUR (\",B%d,\")\")",
                        internalPrjTeamMemberRow, internalPrjTeamMemberRow));
    }

    protected void putHeadingToMatrix(int currentRow) {
        this.headingRow = currentRow;
        putValueToMatrixAt(COL.A, currentRow++, String.format("=A%d", internalPrjPosition.getHeadingRow()));
        putValueToMatrixAt(COL.A, currentRow, I18N.HOUR_SUM);
        putValueToMatrixAt(COL.B, currentRow, I18N.DESCRIPTION);
        putValueToMatrixAt(COL.C, currentRow, I18N.HOUR_RATE);
        putValueToMatrixAt(COL.D, currentRow, I18N.TAX_PERCENTAGE);
        putValueToMatrixAt(COL.E, currentRow, I18N.VAT);
        putValueToMatrixAt(COL.F, currentRow, I18N.PRE_TAX);
    }


    protected void putTravelCostsToMatrix(int currentRow) {
        putValueToMatrixAt(COL.B, currentRow, I18N.TRAVEL_COSTS);
        putReferencesToInternalRowsToMatrix(currentRow, internalPrjPosition.getTravelCostRow());
    }

    private void putReferencesToInternalRowsToMatrix(int currentRow, int internalPrjRow) {
        putValueToMatrixAt(COL.C, currentRow, SpreadsheetFormulas.ROUND(COL.D, internalPrjRow));
        putValueToMatrixAt(COL.D, currentRow, I18N.PERCENT20);
        putValueToMatrixAt(COL.E, currentRow, SpreadsheetFormulas.ROUND(COL.E, internalPrjRow));
        putValueToMatrixAt(COL.F, currentRow, SpreadsheetFormulas.ROUND(COL.F, internalPrjRow));
    }

    protected void putSumLineToMatrix(int currentRow, int fromPos) {
        this.sumRow=currentRow;
        putValueToMatrixAt(COL.C, currentRow,
                SpreadsheetFormulas.SUM(COL.D, fromPos, currentRow-1 ));
        putValueToMatrixAt(COL.E, currentRow,
                SpreadsheetFormulas.SUM(COL.E, fromPos, currentRow-1));
        putValueToMatrixAt(COL.F, currentRow,
                SpreadsheetFormulas.SUM(COL.F, fromPos, currentRow-1));
    }

    private void putValueCheckerToMatrix(int startRow) {
        putValueToMatrixAt(COL.G, startRow,
                String.format("=if(C%d<>D%d,\"Betrag nicht gleich\",\"passt\")", startRow - 1, internalPrjPosition.getSumRow()));
    }
}