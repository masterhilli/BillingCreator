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

    public ProjectSummarySpreadsheetUpdater(String googleDriveIdToExportProject, List<TrackedTime> trackedTimesForProject) {
        exportSpreadsheet = new Spreadsheet(googleDriveIdToExportProject, "2016-01");
        this.trackedTimesPerProject = trackedTimesForProject;
    }

    public void WriteProjectToSpreadSheet(int startingPosition, String workSheetName) {
        this.workSheetName = workSheetName;
        //exportSpreadsheet.addNewWorksheet(workSheetName);
        //exportSpreadsheet.update(workSheetName);
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
        writeEntryToColumnByFormatString("A%d", currentRow, getProjectName());
        writeEntryToColumnByFormatString("C%d", currentRow, "Stundensatz");
        writeEntryToColumnByFormatString("D%d", currentRow, "Netto");
        writeEntryToColumnByFormatString("E%d", currentRow, "UST");
        writeEntryToColumnByFormatString("F%d", currentRow, "Brutto");
    }

    private void writeEntryToColumnByFormatString(String format, int currentRow, String value) {
        System.out.printf("UPDATE: %s with val: %s\n", String.format(format, currentRow), value);
        exportSpreadsheet.insertValueIntoCell(workSheetName, String.format(format, currentRow), value);
    }

    private void writeTrackedTimePerPerson(int currentRow, TrackedTime timetracked) {
        writeEntryToColumnByFormatString("A%d", currentRow, String.format("%.2f", timetracked.getHours()));
        writeEntryToColumnByFormatString("B%d", currentRow, timetracked.getTeamMember());
        writeEntryToColumnByFormatString("C%d", currentRow, Integer.toString(timetracked.getHourRate()));
        writeEntryToColumnByFormatString("D%d", currentRow, String.format("=A%d*C%d", currentRow, currentRow));

        writeUSTCalculationsAndSum(currentRow);
    }

    private void writeTrackedTravelCosts(int currentRow, double sumOfTravelCosts) {
        writeEntryToColumnByFormatString("B%d", currentRow, "Reise- und Nächtigungskosten");
        writeEntryToColumnByFormatString("D%d", currentRow, String.format("%.2f", sumOfTravelCosts));

        writeUSTCalculationsAndSum(currentRow);
    }

    private void writeSums(int currentRow, int startingPosition) {
        writeEntryToColumnByFormatString("D%d", currentRow, String.format("=SUM(D%d:D%d)", currentRow-1, startingPosition));
        writeEntryToColumnByFormatString("E%d", currentRow, String.format("=SUM(E%d:E%d)", currentRow-1, startingPosition));
        writeEntryToColumnByFormatString("F%d", currentRow, String.format("=SUM(F%d:F%d)", currentRow-1, startingPosition));
    }

    private void writeUSTCalculationsAndSum(int currentRow) {

        writeEntryToColumnByFormatString("E%d", currentRow, String.format("=D%d*0.2", currentRow));
        writeEntryToColumnByFormatString("F%d", currentRow, String.format("=D%d+E%d", currentRow, currentRow));
    }

    public int getLastPosition() {
        return lastPosition;
    }

    private String getProjectName() {
        if (trackedTimesPerProject.size() > 1)
            return trackedTimesPerProject.get(0).getProjectName();
        return "";
    }
}
