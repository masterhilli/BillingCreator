package work.billing.Cache.Billing;

import work.billing.Cache.BaseSpreadSheetMatrix;
import work.billing.Cache.ProjectPositions;
import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;
import work.billing.Spreadsheets.SpreadsheetFormulas;

import java.util.List;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public class ProjectSummary extends BaseSpreadSheetMatrix {
    private final List<? extends ProjectPositions> billingProjectMatrices;
    private int firstRowOfInternalProjects = 0;
    private int lastRowOfInternalProjects = 0;
    private int lastPos = 0;

    public ProjectSummary(List<? extends ProjectPositions> billingProjectMatrices, int firstRow, int lastRow) {
        this.billingProjectMatrices = billingProjectMatrices;
        this.firstRowOfInternalProjects = firstRow;
        this.lastRowOfInternalProjects = lastRow;
    }

    public void initialize(int currentRow) {
        int fromPos = currentRow;
        putHeadingForShortSummaryOfBillingProject(currentRow++);
        currentRow = putProjectReferences(currentRow);
        putSummaryToMatrix(currentRow, fromPos);
        putCheckIfValuesAreCorrect(currentRow);
    }

    protected void putHeadingForShortSummaryOfBillingProject(int currentPos) {
        putValueToMatrixAt(COL.B, currentPos, I18N.DESCRIPTION);
        putValueToMatrixAt(COL.C, currentPos, I18N.NET);
        putValueToMatrixAt(COL.D, currentPos, I18N.TAX_PERCENTAGE);
        putValueToMatrixAt(COL.E, currentPos, I18N.VAT);
        putValueToMatrixAt(COL.F, currentPos, I18N.PRE_TAX);
    }

    protected int putProjectReferences(int currentRow) {
        for (ProjectPositions projReference : billingProjectMatrices) {
            putProjectReferenceToMatrix(currentRow++, projReference);
        }
        return currentRow;
    }

    protected void putProjectReferenceToMatrix(int currentRow, ProjectPositions prjPosReference) {
        putValueToMatrixAt(COL.B, currentRow, String.format("=A%d", prjPosReference.getHeadingRow()));
        putValueToMatrixAt(COL.C, currentRow, String.format("=C%d", prjPosReference.getSumRow()));
        putValueToMatrixAt(COL.D, currentRow, I18N.PERCENT20);
        putValueToMatrixAt(COL.E, currentRow, String.format("=E%d", prjPosReference.getSumRow()));
        putValueToMatrixAt(COL.F, currentRow, String.format("=F%d", prjPosReference.getSumRow()));
    }

    protected void putSummaryToMatrix(int currentRow, int fromPos) {
        putValueToMatrixAt(COL.B, currentRow, I18N.SUM_AMOUNT);
        putValueToMatrixAt(COL.C, currentRow, SpreadsheetFormulas.SUM(COL.C, fromPos, currentRow-1));
        putValueToMatrixAt(COL.E, currentRow, SpreadsheetFormulas.SUM(COL.E, fromPos, currentRow-1));
        putValueToMatrixAt(COL.F, currentRow, SpreadsheetFormulas.SUM(COL.F, fromPos, currentRow-1));
    }

    protected void putCheckIfValuesAreCorrect(int currentPos) {
        lastPos = currentPos;
        putValueToMatrixAt(COL.G, currentPos,
                String.format("=if(C%d<>SUM(D%d:D%d)/2,\"Betrag nicht gleich\",\"passt\")", currentPos, firstRowOfInternalProjects, lastRowOfInternalProjects));
    }

    public int getLastPos() {
        return lastPos;
    }
}
