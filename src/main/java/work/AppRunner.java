package work;


import com.google.api.services.drive.model.FileList;
import work.billing.Files.ListSpreadsheets;
import work.billing.Setting.FileSettingReader;
import work.billing.Setting.FileSettings;
import work.billing.Export.ProjectSummarySpreadsheetExporter;
import work.billing.ProjectSpreadSheet.ProjectSpreadsheetToTrackTimeMapper;
import work.billing.Spreadsheets.Spreadsheet;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeAlreadyExistsException;
import work.billing.Timesheet.TrackedTimeSummary;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class AppRunner implements Runnable {

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            switch (args[0]) {
                case "-create":
                    exportProjectDataToSpreadSheet(args[1], args[2]);
                    break;
                case "-list":
                    TestFileIdFetch(args[1]);
                    break;
                default:
                    printHelpInformation();
            }
        } else {
            printHelpInformation();
        }
    }

    private static void printHelpInformation() {
        System.out.println("Please provide 3 args: ");
        System.out.println("-create <worksheetname> <pathToSettingsFile>");
        System.out.println(" or ");
        System.out.println("-list \"filterstring\"");
    }

    private static void exportProjectDataToSpreadSheet(String worksheetName, String pathToSetting) {

        long startTime = System.currentTimeMillis();

        FileSettings settings = FileSettingReader.ReadFileSettingsFromFile(pathToSetting);
        TrackedTimeSummary trackedTimeSum = new TrackedTimeSummary();
        for (String key : settings.importFileId) {
            Spreadsheet timeSheet = new Spreadsheet(key, worksheetName);
            TrackedTime timeTracked = ProjectSpreadsheetToTrackTimeMapper.createTrackedTimeFromSpreadsheet(
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

        List<Thread> threads = new ArrayList<>();
        for (Integer column : matrixCreator.cellMatrix.keySet()) {
            Thread t = new Thread(new AppRunner(column, matrixCreator.cellMatrix.get(column), spreadsheet, worksheetName));
            t.start();
            threads.add(t);

        }

        boolean threadsStillRunning;
        do {
            threadsStillRunning = false;
            for (Thread t : threads) {
                threadsStillRunning = threadsStillRunning || t.isAlive();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print(".");
        } while (threadsStillRunning);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = (stopTime - startTime)/1000;
        System.out.println("Elapsed time: " + elapsedTime + "s");
    }

    public AppRunner(Integer col, AbstractMap<Integer, String> rows, Spreadsheet spreadsheet, String worksheetName) {
        this.column = col;
        this.rows = rows;
        this.spreadsheet = spreadsheet;
        this.worksheetName = worksheetName;
    }
    private Integer column;
    private AbstractMap<Integer, String> rows;
    private Spreadsheet spreadsheet;
    private String worksheetName;

    @Override
    public void run() {
        for (Integer row : rows.keySet()) {
            spreadsheet.insertValueIntoCell(worksheetName, column, row, rows.get(row));
        }
        System.out.println("\nCOLUMN finished [" + column.toString() + "]");
    }

    private static void TestFileIdFetch(String arg) throws IOException {
        FileList myFiles = ListSpreadsheets.retrieveAllFiles(arg);
    }

}