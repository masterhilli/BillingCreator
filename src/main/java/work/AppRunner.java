package work;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import google.api.GoogleServiceConnector;
import google.api.GoogleSpreadSheetFeed;
import google.api.spreadsheet.SheetsQuickStart;
import work.billing.Setting.FileSettingReader;
import work.billing.Setting.FileSettings;
import work.billing.Spreadsheets.ProjectsheetToTrackTimeMapper;
import work.billing.Spreadsheets.Spreadsheet;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeAlreadyExistsException;
import work.billing.Timesheet.TrackedTimeSummary;

import java.io.IOException;
import java.util.HashMap;

public class AppRunner {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Please provide 2 args: <worksheetname> <pathToSettingsFile>");
            return;
        }
        String worksheetName = args[0];
        String pathToSettingFile = args[1];
        FileSettings fileSettings = FileSettingReader.ReadFileSettingsFromFile(pathToSettingFile);

        TrackedTimeSummary trackedTimeSum = new TrackedTimeSummary();
        for (String key : fileSettings.importFileId) {
            Spreadsheet timeSheet = new Spreadsheet(key, worksheetName);
            TrackedTime timeTracked = ProjectsheetToTrackTimeMapper.createTrackedTimeFromSpreadsheet(
                    timeSheet, worksheetName, fileSettings.getHourRateAsHashMapPerTeamMember());
            try {
                trackedTimeSum.addTrackedTime(timeTracked);
            } catch (TrackedTimeAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
        trackedTimeSum.printTimesForAllProjects();
        trackedTimeSum.printTimesForAllTeamMembers();

        // no glue if we still will need them.
        //TestMethodsForSpreadSheets();
    }

    private static void TestMethodsForSpreadSheets() {
        Spreadsheet mySpreadsheet = new Spreadsheet("1MBc1Uvv4Wfyw31mwoGnrEzfCaXxcd1BT-aLg0x1VS_Y");
        System.out.println(mySpreadsheet.toString());
        mySpreadsheet.addNewWorksheet("MartinsSpreadSheet");
        mySpreadsheet.copyWorksheet("Tabellenblatt1", "CopiedWorksheet");
        mySpreadsheet.insertValueIntoCell("Tabellenblatt2", "D10", "My name is Martin");
        String value = mySpreadsheet.receiveValueAtKey("Tabellenblatt2", "E11");

        int valueAsInteger ;
        try {
            valueAsInteger = Integer.parseInt(value);
            valueAsInteger++;
            value = Integer.toString(valueAsInteger);
        } catch (NumberFormatException e) {
            value = "Could not convert item";
        }
        mySpreadsheet.insertValueIntoCell("Tabellenblatt2", "E11", value);

        mySpreadsheet.update();
        System.out.println(mySpreadsheet.toString());
        mySpreadsheet.deleteWorksheet("MartinsSpreadSheet");
        mySpreadsheet.deleteWorksheet("CopiedWorksheet");
    }

}