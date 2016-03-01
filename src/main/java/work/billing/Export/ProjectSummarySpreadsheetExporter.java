package work.billing.Export;

import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mhillbrand on 2/22/2016.
 */
public class ProjectSummarySpreadsheetExporter {
    public AbstractMap<Integer, AbstractMap<Integer, String>> cellMatrix = new ConcurrentHashMap<>();
    private AbstractMap<String, Integer> hourRatePerTeamMember = new HashMap<>();
    private List<Integer> listOfSums = new ArrayList<>();
    private TrackedTimeSummary trackedTimeSum;
    private int lastPosition = 0;
    private int posForSumUp = 0;
    private List<ProjectRowReference> rowsForReferencesPerProject = new ArrayList<>();


    public ProjectSummarySpreadsheetExporter(TrackedTimeSummary trackedTimeSum, AbstractMap<String, Integer> hourRatePerTeamMember) {
        this.trackedTimeSum = trackedTimeSum;
        this.hourRatePerTeamMember = hourRatePerTeamMember;
    }

    private void WriteProjectToSpreadSheet(List<TrackedTime> trackedTimesPerProject, int startingPosition, ProjectRowReference rowReferences) {
        writeHeading(startingPosition++,trackedTimesPerProject, rowReferences);
        int currentPosition = startingPosition;
        double sumOfTravelCosts = 0.0;
        for (TrackedTime timetracked : trackedTimesPerProject) {
            writeTrackedTimePerPerson(currentPosition++, timetracked);
        }
        writeTrackedTravelCosts(currentPosition++, sumOfTravelCosts, rowReferences);
        writeSums(currentPosition++, startingPosition, rowReferences);
        lastPosition = currentPosition + 1;
    }

    private void writeHeading(int currentRow, List<TrackedTime> trackedTimesPerProject, ProjectRowReference rowReferences) {
        rowReferences.setProjectNameRow(currentRow);
        writeEntryToColumnByFormatString(1, currentRow, getProjectName(trackedTimesPerProject));
        writeEntryToColumnByFormatString(3, currentRow, "Stundensatz");
        writeEntryToColumnByFormatString(4, currentRow, "Netto");
        writeEntryToColumnByFormatString(5, currentRow, "UST");
        writeEntryToColumnByFormatString(6, currentRow, "Brutto");
    }

    private void writeEntryToColumnByFormatString(int column, int currentRow, String value) {
        Integer col = column;
        if (this.cellMatrix.get(col) == null) {
            this.cellMatrix.put(col, new ConcurrentHashMap<>());
        }
        this.cellMatrix.get(col).put(currentRow, value);
    }

    private void writeTrackedTimePerPerson(int currentRow, TrackedTime timetracked) {
        writeEntryToColumnByFormatString(1, currentRow, String.format("%.2f", timetracked.getHours()));
        writeEntryToColumnByFormatString(2, currentRow, timetracked.getTeamMember());
        writeEntryToColumnByFormatString(3, currentRow, Integer.toString(timetracked.getHourRate()));
        writeEntryToColumnByFormatString(4, currentRow, String.format("=A%d*C%d", currentRow, currentRow));

        writeUSTCalculationsAndSum(currentRow);
    }

    private void writeTrackedTravelCosts(int currentRow, double sumOfTravelCosts, ProjectRowReference rowReferences) {
        rowReferences.setTravelCostRow(currentRow);
        writeEntryToColumnByFormatString(2, currentRow, "Reise- und Nächtigungskosten");
        writeEntryToColumnByFormatString(4, currentRow, String.format("%.2f", sumOfTravelCosts));

        writeUSTCalculationsAndSum(currentRow);
    }

    private void writeSums(int currentRow, int startingPosition, ProjectRowReference rowReferences) {
        rowReferences.setSumRow(currentRow);
        writeEntryToColumnByFormatString(4, currentRow, String.format("=SUM(D%d:D%d)", currentRow - 1, startingPosition));
        writeEntryToColumnByFormatString(5, currentRow, String.format("=SUM(E%d:E%d)", currentRow - 1, startingPosition));
        writeEntryToColumnByFormatString(6, currentRow, String.format("=SUM(F%d:F%d)", currentRow - 1, startingPosition));
    }

    private void writeUSTCalculationsAndSum(int currentRow) {
        writeEntryToColumnByFormatString(5, currentRow, String.format("=D%d*0.2", currentRow));
        writeEntryToColumnByFormatString(6, currentRow, String.format("=D%d+E%d", currentRow, currentRow));
    }

    private int getLastPosition() {
        return lastPosition;
    }


    private String getProjectName(List<TrackedTime> trackedTimesPerProject) {
        if (trackedTimesPerProject.size() >= 1)
            return trackedTimesPerProject.get(0).getProjectName();
        return "";
    }

    public void createBillingSpreadsheet( ) {
        int startPos = this.hourRatePerTeamMember.keySet().size()+3;
        startPos = writeProjectsToSpreadsheet(startPos);
        writeSumOfTimesPerTeamMembersToSpreadsheet(1,  startPos);
        int endOfSearch = startPos;
        writeSummaryAtTheEndOfTheList(startPos+2, this.hourRatePerTeamMember.keySet().size()+1, endOfSearch);

        startPos = writeProjectInformationAgain(startPos + 10);

        writeProjectShortSummary(startPos+2);
        String valueForSumOfProjects = "=";
        for (ProjectRowReference prjRowRef : rowsForReferencesPerProject) {
            if (valueForSumOfProjects.length() != 1) {
                valueForSumOfProjects+="+";
            }
            valueForSumOfProjects += String.format("C%d", prjRowRef.getSumRowFor2ndRun());
        }
        writeEntryToColumnByFormatString(4, posForSumUp, valueForSumOfProjects);
    }

    private void writeProjectShortSummary(int startPos) {
        writeEntryToColumnByFormatString(2, startPos, "Beschreibung");
        writeEntryToColumnByFormatString(3, startPos, "Netto");
        writeEntryToColumnByFormatString(4, startPos, "Steuersatz");
        writeEntryToColumnByFormatString(5, startPos, "Ust.");
        writeEntryToColumnByFormatString(6, startPos, "Brutto");
        startPos++;
        int sumStartPos = startPos;
        for (ProjectRowReference prjRowInfo: rowsForReferencesPerProject) {
            writeEntryToColumnByFormatString(2, startPos, String.format("=A%d", prjRowInfo.getProjectNameRow2nd()));
            writeEntryToColumnByFormatString(3, startPos, String.format("=C%d", prjRowInfo.getSumRowFor2ndRun()));
            writeEntryToColumnByFormatString(4, startPos, "20%");
            writeEntryToColumnByFormatString(5, startPos, String.format("=E%d", prjRowInfo.getSumRowFor2ndRun()));
            writeEntryToColumnByFormatString(6, startPos, String.format("=F%d", prjRowInfo.getSumRowFor2ndRun()));
            startPos++;
        }

        writeEntryToColumnByFormatString(2, startPos, "Gesamtbetrag");
        writeEntryToColumnByFormatString(3, startPos, String.format("=SUM(C%d:C%d)", sumStartPos, startPos-1));

        writeEntryToColumnByFormatString(5, startPos, String.format("=SUM(E%d:E%d)", sumStartPos, startPos-1));
        writeEntryToColumnByFormatString(6, startPos, String.format("=SUM(F%d:F%d)", sumStartPos, startPos-1));

        int minRegion = Integer.MAX_VALUE;
        int maxRegion = Integer.MIN_VALUE;
        for (ProjectRowReference prjRowRef : rowsForReferencesPerProject) {
            minRegion = Integer.min(minRegion, prjRowRef.getProjectNameRow());
            maxRegion = Integer.max(maxRegion, prjRowRef.getSumRow());
        }
        writeEntryToColumnByFormatString(7, startPos,
                String.format("=if(C%d<>sum(D%d:D%d)/2,\"Betrag nicht gleich\",\"passt\")", startPos, minRegion, maxRegion));

    }

    private int writeProjectInformationAgain(int startPos) {
        for (ProjectRowReference prjRowRef : rowsForReferencesPerProject){
            prjRowRef.setProjectNameRow2nd(startPos);
            writeEntryToColumnByFormatString(1, startPos++, String.format("=A%d", prjRowRef.getProjectNameRow()));
            int headPos = startPos;
            writeFirstLine( startPos++);
            startPos = writeUsers(prjRowRef, startPos);
            writeTravelCosts(prjRowRef, startPos++);
            writeSums(prjRowRef, startPos++, headPos);
            writeEntryToColumnByFormatString(7, startPos,
                    String.format("=if(C%d<>D%d,\"Betrag nicht gleich\",\"passt\")", startPos-1, prjRowRef.getSumRow()));
            startPos++;
        }
        return startPos;
    }

    private void writeSums(ProjectRowReference prjRowRef, int startPos, int headPos) {
        writeEntryToColumnByFormatString(3, startPos,
                String.format("=SUM(C%d:C%d)", headPos, startPos-1));
        writeEntryToColumnByFormatString(5, startPos,
                String.format("=SUM(E%d:F%d)", headPos, startPos-1));
        writeEntryToColumnByFormatString(6, startPos,
                String.format("=SUM(F%d:F%d)", headPos, startPos-1));
        prjRowRef.setSumRowFor2ndRun(startPos);
    }

    private void writeTravelCosts(ProjectRowReference prjRowRef, int startPos) {
        writeEntryToColumnByFormatString(2, startPos, "Reise und Nächtigungskosten");
        writeCalculations(startPos, prjRowRef.getTravelCostRow());
    }

    private int writeUsers(ProjectRowReference prjRowRef, int startPos) {
        int i;
        int to = (prjRowRef.getTravelCostRow()- prjRowRef.getProjectNameRow())-1;
        for ( i=0; i < to; i++)
        {
            int rowOfUser = prjRowRef.getProjectNameRow()+(i+1);
            writeEntryToColumnByFormatString(1, startPos+i, String.format("=A%d", rowOfUser));
            writeEntryToColumnByFormatString(2, startPos+i,
                    String.format("=CONCATENATE(\"Stunden Arbeitszeit zu einem Stundensatz von \",C%d,\" EUR (\",B%d,\")\")", rowOfUser, rowOfUser));
            writeCalculations(startPos+i, rowOfUser);
        }
        return startPos+i;
    }

    private void writeCalculations(int startPos, int rowOfData) {
        writeEntryToColumnByFormatString(3, startPos, String.format("=ROUND(D%d,2)", rowOfData));
        writeEntryToColumnByFormatString(4, startPos, "20%");
        writeEntryToColumnByFormatString(5, startPos, String.format("=ROUND(E%d,2)", rowOfData));
        writeEntryToColumnByFormatString(6, startPos, String.format("=ROUND(F%d,2)", rowOfData));
    }

    private void writeFirstLine(int startPos) {
        writeEntryToColumnByFormatString(1, startPos, "Anz.");
        writeEntryToColumnByFormatString(2, startPos, "Beschreibung");
        writeEntryToColumnByFormatString(3, startPos, "Netto");
        writeEntryToColumnByFormatString(4, startPos, "Steuersatz");
        writeEntryToColumnByFormatString(5, startPos, "Ust.");
        writeEntryToColumnByFormatString(6, startPos, "Brutto");
    }

    private void writeSumOfTimesPerTeamMembersToSpreadsheet(int startPos, int endPosOfProjects) {
        int start = startPos;
        int size = this.hourRatePerTeamMember.keySet().size();
        for (String teamMember : this.hourRatePerTeamMember.keySet()) {
            String value = getFormatForSUMIF(size+1, endPosOfProjects, startPos);
            writeEntryToColumnByFormatString(1, startPos, value);
            writeEntryToColumnByFormatString(2, startPos++, teamMember);
        }
        writeEntryToColumnByFormatString(1,startPos, String.format("=SUM(A%d:A%d)", start, startPos-1));
    }

    private String getFormatForSUMIF(int startPos, int endPosOfProjects, int valueToCheckPos) {
        return String.format("=SUMIF($B$%d:$B$%d,B%d,$A$%d:$A$%d)",
                startPos, endPosOfProjects, valueToCheckPos,
                startPos, endPosOfProjects);
    }

    private int writeProjectsToSpreadsheet(int startPos) {
        for (String projectName : this.trackedTimeSum.getProjectNames()) {
            // TODO: check why we print always the same project name here!
            ProjectRowReference rowReference = new ProjectRowReference();
            WriteProjectToSpreadSheet(this.trackedTimeSum.receiveTrackedTimesPerProject(projectName), startPos, rowReference);
            rowsForReferencesPerProject.add(rowReference);
            listOfSums.add(getLastPosition() - 2);
            startPos = getLastPosition() + 1;
        }
        return startPos;
    }


    private int writeSummaryAtTheEndOfTheList(int row, int startSearchPos, int endSearchPos) {
        writeEntryToColumnByFormatString(1, row, "GESAMTUMSATZ");
        writeEntryToColumnByFormatString(4, row, String.format("=SUM(D1:D%d)/2", row-1));
        writeEntryToColumnByFormatString(5, row, String.format("=SUM(E1:E%d)/2", row-1));
        writeEntryToColumnByFormatString(6, row, String.format("=SUM(F1:F%d)/2", row-1));

        writeEntryToColumnByFormatString(3, row+1,
                String.format("=if(D%d<>D%d,\"da stimmt was nicht\",\"Summen passen\")", row, ++row));

        posForSumUp = row;
        row++;

        writeEntryToColumnByFormatString(2, row, "Test User");
        writeEntryToColumnByFormatString(3, row, "Eigene Stunden");
        writeEntryToColumnByFormatString(4, row,
                getFormatForSUMIF(startSearchPos, endSearchPos, row));
        writeEntryToColumnByFormatString(5, row, "50");
        writeEntryToColumnByFormatString(7, row, String.format("=D%d*E%d", row, row++));

        writeEntryToColumnByFormatString(5, row++, "gerundet");

        writeEntryToColumnByFormatString(3, row, "Teamstunden");
        writeEntryToColumnByFormatString(4, row,
                String.format("=A%d-D%d", hourRatePerTeamMember.size()+1, row-2));
        writeEntryToColumnByFormatString(5, row,
                String.format("=IF(MOD(D%d,50)=0,D%d,(ROUNDDOWN(D%d/50,0)+1)*50)",
                        row, row, row));
        writeEntryToColumnByFormatString(6, row, "4.00");
        writeEntryToColumnByFormatString(7, row,
                String.format("=MIN(E%d*F%d,G%d)", row, row, row-2));
        row++;

        writeEntryToColumnByFormatString( 3, row, "Summe für Rechnung");
        writeEntryToColumnByFormatString( 7, row,
                String.format("=SUM(G%d:G%d)", row-3, row-1));
        row++;

        writeEntryToColumnByFormatString( 3, row, "effektiver Stundensatz");
        writeEntryToColumnByFormatString( 7, row, String.format("=G%d/D%d", row-1, row-4));

        return row+3;
    }
}
