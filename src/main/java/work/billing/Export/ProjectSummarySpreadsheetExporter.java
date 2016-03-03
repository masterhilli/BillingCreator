package work.billing.Export;

import work.billing.Timesheet.TrackedTimeSummary;

import java.util.*;

/**
 * Created by mhillbrand on 2/22/2016.
 */
public class ProjectSummarySpreadsheetExporter extends BaseSpreadSheetMatrix {

    private AbstractMap<String, Integer> hourRatePerTeamMember = new HashMap<>();
    private int lastPosition = 0;
    private int posForSumUp = 0;
    private TrackedTimeSummary trackedTimeSum;
    private AbstractList<ProjectRowReference> rowsForReferencesPerProject;


    public ProjectSummarySpreadsheetExporter(TrackedTimeSummary trackedTimeSum, AbstractMap<String, Integer> hourRatePerTeamMember) {
        this.trackedTimeSum = trackedTimeSum;
        this.hourRatePerTeamMember = hourRatePerTeamMember;
    }

    public void createBillingSpreadsheet() {
        AbstractList<String> teamMembers = new ArrayList<>();
        teamMembers.addAll(this.hourRatePerTeamMember.keySet());

        //**************************** SECTION TO CREATE INTERNAL PRJ PRESENTATION ***********************
        int startPosInternalProjects = teamMembers.size()+2;

        InternalProjectsListingMatrix internalPrjMatrix = new InternalProjectsListingMatrix(trackedTimeSum);
        internalPrjMatrix.initializeMatrix(startPosInternalProjects);
        int endPos = internalPrjMatrix.getLastPos();
        rowsForReferencesPerProject = internalPrjMatrix.getRowReferences();
        //************************************************************************************************
        //************************** SECTION TO CREATE TEAM MEMBER OVERVIEW ******************************

        TeamMemberOverviewMatrix teamMemberMatrix = new TeamMemberOverviewMatrix(teamMembers);
        teamMemberMatrix.putTeamMembersAndTimesToMatrix(startPosInternalProjects, endPos);
        //************************************************************************************************
        int startPos = endPos;
        int endOfSearch = endPos;
        writeSummaryAtTheEndOfTheList(startPos + 2, this.hourRatePerTeamMember.keySet().size() + 1, endOfSearch);

        startPos = writeProjectInformationAgain(startPos + 10);

        writeProjectShortSummary(startPos + 2);
        String valueForSumOfProjects = "=";
        for (ProjectRowReference prjRowRef : rowsForReferencesPerProject) {
            if (valueForSumOfProjects.length() != 1) {
                valueForSumOfProjects += "+";
            }
            valueForSumOfProjects += String.format("C%d", prjRowRef.getSumRowFor2ndRun());
        }
        putValueToMatrixAt(4, posForSumUp, valueForSumOfProjects);
    }

    private void writeProjectShortSummary(int startPos) {
        putValueToMatrixAt(2, startPos, "Beschreibung");
        putValueToMatrixAt(3, startPos, "Netto");
        putValueToMatrixAt(4, startPos, "Steuersatz");
        putValueToMatrixAt(5, startPos, "Ust.");
        putValueToMatrixAt(6, startPos, "Brutto");
        startPos++;
        int sumStartPos = startPos;
        for (ProjectRowReference prjRowInfo : rowsForReferencesPerProject) {
            putValueToMatrixAt(2, startPos, String.format("=A%d", prjRowInfo.getProjectNameRow2nd()));
            putValueToMatrixAt(3, startPos, String.format("=C%d", prjRowInfo.getSumRowFor2ndRun()));
            putValueToMatrixAt(4, startPos, I18N.PERCENT20);
            putValueToMatrixAt(5, startPos, String.format("=E%d", prjRowInfo.getSumRowFor2ndRun()));
            putValueToMatrixAt(6, startPos, String.format("=F%d", prjRowInfo.getSumRowFor2ndRun()));
            startPos++;
        }

        putValueToMatrixAt(2, startPos, I18N.SUM_AMOUNT);
        putValueToMatrixAt(3, startPos, String.format("=SUM(C%d:C%d)", sumStartPos, startPos - 1));

        putValueToMatrixAt(5, startPos, String.format("=SUM(E%d:E%d)", sumStartPos, startPos - 1));
        putValueToMatrixAt(6, startPos, String.format("=SUM(F%d:F%d)", sumStartPos, startPos - 1));

        int minRegion = Integer.MAX_VALUE;
        int maxRegion = Integer.MIN_VALUE;
        for (ProjectRowReference prjRowRef : rowsForReferencesPerProject) {
            minRegion = Integer.min(minRegion, prjRowRef.getProjectNameRow());
            maxRegion = Integer.max(maxRegion, prjRowRef.getSumRow());
        }
        putValueToMatrixAt(7, startPos,
                String.format("=if(C%d<>sum(D%d:D%d)/2,\"Betrag nicht gleich\",\"passt\")", startPos, minRegion, maxRegion));

    }

    private int writeProjectInformationAgain(int startPos) {
        for (ProjectRowReference prjRowRef : rowsForReferencesPerProject) {
            prjRowRef.setProjectNameRow2nd(startPos);
            putValueToMatrixAt(1, startPos++, String.format("=A%d", prjRowRef.getProjectNameRow()));
            int headPos = startPos;
            writeFirstLine(startPos++);
            startPos = writeUsers(prjRowRef, startPos);
            writeTravelCosts(prjRowRef, startPos++);
            writeSums(prjRowRef, startPos++, headPos);
            putValueToMatrixAt(7, startPos,
                    String.format("=if(C%d<>D%d,\"Betrag nicht gleich\",\"passt\")", startPos - 1, prjRowRef.getSumRow()));
            startPos++;
        }
        return startPos;
    }

    private void writeSums(ProjectRowReference prjRowRef, int startPos, int headPos) {
        putValueToMatrixAt(3, startPos,
                String.format("=SUM(C%d:C%d)", headPos, startPos - 1));
        putValueToMatrixAt(5, startPos,
                String.format("=SUM(E%d:F%d)", headPos, startPos - 1));
        putValueToMatrixAt(6, startPos,
                String.format("=SUM(F%d:F%d)", headPos, startPos - 1));
        prjRowRef.setSumRowFor2ndRun(startPos);
    }

    private void writeTravelCosts(ProjectRowReference prjRowRef, int startPos) {
        putValueToMatrixAt(2, startPos, I18N.TRAVEL_COSTS);
        writeCalculations(startPos, prjRowRef.getTravelCostRow());
    }

    private int writeUsers(ProjectRowReference prjRowRef, int startPos) {
        int i;
        int to = (prjRowRef.getTravelCostRow() - prjRowRef.getProjectNameRow()) - 1;
        for (i = 0; i < to; i++) {
            int rowOfUser = prjRowRef.getProjectNameRow() + (i + 1);
            putValueToMatrixAt(1, startPos + i, String.format("=A%d", rowOfUser));
            putValueToMatrixAt(2, startPos + i,
                    String.format("=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C%d,\" EUR (\",B%d,\")\")", rowOfUser, rowOfUser));
            writeCalculations(startPos + i, rowOfUser);
        }
        return startPos + i;
    }

    private void writeCalculations(int startPos, int rowOfData) {
        putValueToMatrixAt(3, startPos, String.format("=ROUND(D%d,2)", rowOfData));
        putValueToMatrixAt(4, startPos, I18N.PERCENT20);
        putValueToMatrixAt(5, startPos, String.format("=ROUND(E%d,2)", rowOfData));
        putValueToMatrixAt(6, startPos, String.format("=ROUND(F%d,2)", rowOfData));
    }

    private void writeFirstLine(int startPos) {
        putValueToMatrixAt(1, startPos, "Anz.");
        putValueToMatrixAt(2, startPos, "Beschreibung");
        putValueToMatrixAt(3, startPos, I18N.NETTO);
        putValueToMatrixAt(4, startPos, "Steuersatz");
        putValueToMatrixAt(5, startPos, I18N.UST);
        putValueToMatrixAt(6, startPos, I18N.BRUTTO);
    }


    private int writeSummaryAtTheEndOfTheList(int row, int startSearchPos, int endSearchPos) {
        putValueToMatrixAt(1, row, I18N.SUM_AMOUNT.toUpperCase());
        putValueToMatrixAt(4, row, String.format("=SUM(D1:D%d)/2", row - 1));
        putValueToMatrixAt(5, row, String.format("=SUM(E1:E%d)/2", row - 1));
        putValueToMatrixAt(6, row, String.format("=SUM(F1:F%d)/2", row - 1));

        putValueToMatrixAt(3, row + 1,
                String.format("=if(D%d<>D%d,\"da stimmt was nicht\",\"Summen passen\")", row, ++row));

        posForSumUp = row;
        row++;

        putValueToMatrixAt(2, row, "Felix Schwenk");
        putValueToMatrixAt(3, row, "Eigene Stunden");
        putValueToMatrixAt(4, row,
                SpreadsheetFormulas.SUMIF(COL.B.toString(), COL.A.toString(), startSearchPos, endSearchPos,
                        COL.B.toString(), row));
        putValueToMatrixAt(5, row, "50");
        putValueToMatrixAt(7, row, String.format("=D%d*E%d", row, row++));

        putValueToMatrixAt(5, row++, "gerundet");

        putValueToMatrixAt(3, row, "Teamstunden");
        putValueToMatrixAt(4, row,
                String.format("=A%d-D%d", hourRatePerTeamMember.size() + 1, row - 2));
        putValueToMatrixAt(5, row,
                String.format("=IF(MOD(D%d,50)=0,D%d,(ROUNDDOWN(D%d/50,0)+1)*50)",
                        row, row, row));
        putValueToMatrixAt(6, row, "4.00");
        putValueToMatrixAt(7, row,
                String.format("=MIN(E%d*F%d,G%d)", row, row, row - 2));
        row++;

        putValueToMatrixAt(3, row, "Summe für Rechnung");
        putValueToMatrixAt(7, row,
                String.format("=SUM(G%d:G%d)", row - 3, row - 1));
        row++;

        putValueToMatrixAt(3, row, "effektiver Stundensatz");
        putValueToMatrixAt(7, row, String.format("=G%d/D%d", row - 1, row - 4));

        return row + 3;
    }
}
