package work.billing.Spreadsheets;

import work.billing.Timesheet.TrackedTime;

import java.util.List;

/**
 * Created by mhillbrand on 2/22/2016.
 */
public class ProjectSummarySpreadsheetUpdater {
    private List<TrackedTime> trackedTimesPerProject;
    private Spreadsheet exportSpreadsheet;
    private int lastPosition = 0;
    private String workSheetName = "sheet 1s";
    private ProjectRowReference rowReferences = new ProjectRowReference();

    public ProjectSummarySpreadsheetUpdater(String googleDriveIdToExportProject, List<TrackedTime> trackedTimesForProject) {
        exportSpreadsheet = new Spreadsheet(googleDriveIdToExportProject, "2016-01");
        this.trackedTimesPerProject = trackedTimesForProject;
    }

    public void WriteProjectToSpreadSheet(int startingPosition, String workSheetName) {
        this.workSheetName = workSheetName;
        writeHeading(startingPosition++);
        int currentPosition = startingPosition;
        double sumOfTravelCosts = 0.0;
        for (TrackedTime timetracked : trackedTimesPerProject) {
            writeTrackedTimePerPerson(currentPosition++, timetracked);
        }
        writeTrackedTravelCosts(currentPosition++, sumOfTravelCosts);
        writeSums(currentPosition++, startingPosition);
        lastPosition = currentPosition+1;
    }


    private void writeHeading(int currentRow) {
        rowReferences.setProjectNameRow(currentRow);
        writeEntryToColumnByFormatString(1, currentRow, getProjectName());
        writeEntryToColumnByFormatString(3, currentRow, "Stundensatz");
        writeEntryToColumnByFormatString(4, currentRow, "Netto");
        writeEntryToColumnByFormatString(5, currentRow, "UST");
        writeEntryToColumnByFormatString(6, currentRow, "Brutto");
    }

    private void writeEntryToColumnByFormatString(int column, int currentRow, String value) {
        exportSpreadsheet.insertValueIntoCell(workSheetName, column, currentRow, value);
    }

    private void writeTrackedTimePerPerson(int currentRow, TrackedTime timetracked) {
        writeEntryToColumnByFormatString(1, currentRow, String.format("%.2f", timetracked.getHours()));
        writeEntryToColumnByFormatString(2, currentRow, timetracked.getTeamMember());
        writeEntryToColumnByFormatString(3, currentRow, Integer.toString(timetracked.getHourRate()));
        writeEntryToColumnByFormatString(4, currentRow, String.format("=A%d*C%d", currentRow, currentRow));

        writeUSTCalculationsAndSum(currentRow);
    }

    private void writeTrackedTravelCosts(int currentRow, double sumOfTravelCosts) {
        rowReferences.setTravelCostRow(currentRow);
        writeEntryToColumnByFormatString(2, currentRow, "Reise- und Nächtigungskosten");
        writeEntryToColumnByFormatString(4, currentRow, String.format("%.2f", sumOfTravelCosts));

        writeUSTCalculationsAndSum(currentRow);
    }

    private void writeSums(int currentRow, int startingPosition) {
        rowReferences.setSumRow(currentRow);
        writeEntryToColumnByFormatString(4, currentRow, String.format("=SUM(D%d:D%d)", currentRow-1, startingPosition));
        writeEntryToColumnByFormatString(5, currentRow, String.format("=SUM(E%d:E%d)", currentRow-1, startingPosition));
        writeEntryToColumnByFormatString(6, currentRow, String.format("=SUM(F%d:F%d)", currentRow-1, startingPosition));
    }

    private void writeUSTCalculationsAndSum(int currentRow) {

        writeEntryToColumnByFormatString(5, currentRow, String.format("=D%d*0.2", currentRow));
        writeEntryToColumnByFormatString(6, currentRow, String.format("=D%d+E%d", currentRow, currentRow));
    }

    public int getLastPosition() {
        return lastPosition;
    }

    private String getProjectName() {
        if (trackedTimesPerProject.size() >= 1)
            return trackedTimesPerProject.get(0).getProjectName();
        return "";
    }

    public ProjectRowReference getRowInformationForReferences() {
        return rowReferences;
    }
}
