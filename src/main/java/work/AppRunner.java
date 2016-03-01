package work;


import work.billing.Setting.FileSettingReader;
import work.billing.Setting.FileSettings;
import work.billing.Setting.HourRate;
import work.billing.Spreadsheets.ProjectRowReference;
import work.billing.Spreadsheets.ProjectSummarySpreadsheetExporter;
import work.billing.Spreadsheets.ProjectsheetToTrackTimeMapper;
import work.billing.Spreadsheets.Spreadsheet;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeAlreadyExistsException;
import work.billing.Timesheet.TrackedTimeSummary;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppRunner {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Please provide 2 args: <worksheetname> <pathToSettingsFile>");
            return;
        }

        FileSettings settings = FileSettingReader.ReadFileSettingsFromFile(args[1]);
        String worksheetName = args[0];
        TrackedTimeSummary trackedTimeSum = new TrackedTimeSummary();
        for (String key : settings.importFileId) {
            Spreadsheet timeSheet = new Spreadsheet(key, worksheetName);
            TrackedTime timeTracked = ProjectsheetToTrackTimeMapper.createTrackedTimeFromSpreadsheet(
                    timeSheet, worksheetName, settings.getHourRateAsHashMapPerTeamMember());
            try {
                trackedTimeSum.addTrackedTime(timeTracked);
            } catch (TrackedTimeAlreadyExistsException e) {
                e.printStackTrace();
            }
        }

        ProjectSummarySpreadsheetExporter matrixCreator = new ProjectSummarySpreadsheetExporter(trackedTimeSum,settings.getHourRateAsHashMapPerTeamMember());

        matrixCreator.createBillingSpreadsheet();

        Spreadsheet spreadsheet = new Spreadsheet(settings.exportFileId, worksheetName);

        for (Integer column : matrixCreator.cellMatrix.keySet()) {
            AbstractMap<Integer, String> rows = matrixCreator.cellMatrix.get(column);
            for (Integer row : rows.keySet()) {
                //System.out.printf("[%d/%d] %s\t", column.intValue(), row.intValue(), rows.get(row));
                spreadsheet.insertValueIntoCell(worksheetName, column.intValue(), row.intValue(), rows.get(row));
            }
            System.out.println("COLUMN finished [" + column.toString() + "]");
        }
    }


}