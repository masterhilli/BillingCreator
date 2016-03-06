package work;


import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.api.services.drive.model.FileList;
import work.billing.Cache.BaseSpreadSheetMatrix;
import work.billing.Files.ListSpreadsheets;
import work.billing.Setting.FileSettingReader;
import work.billing.Setting.FileSettings;
import work.billing.Cache.MonthlyReportCache;
import work.billing.ProjectSpreadSheet.ProjectSpreadsheetToTrackTimeMapper;
import work.billing.Spreadsheets.COL;
import work.billing.Spreadsheets.Spreadsheet;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeAlreadyExistsException;
import work.billing.Timesheet.TrackedTimeSummary;

import java.io.IOException;
import java.util.*;

public class AppRunner implements Runnable {

    private final BaseSpreadSheetMatrix cachedMatrix;
    private final Spreadsheet spreadSheet;
    private final String workSheetName;

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

        MonthlyReportCache reportCache = new MonthlyReportCache(trackedTimeSum,settings.getHourRateAsHashMapPerTeamMember());

        reportCache.initializeCache();


        Spreadsheet spreadsheet = new Spreadsheet(settings.exportFileId, worksheetName);

        List<Thread> threads = new ArrayList<>();
        for (BaseSpreadSheetMatrix cachedMatrix : reportCache.getSpreadSheetMatrixList()) {
            Thread t = new Thread(new AppRunner(cachedMatrix, spreadsheet, worksheetName));
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

    public AppRunner (BaseSpreadSheetMatrix cachedMatrix, Spreadsheet spreadsheet, String worksheetName) {
        this.cachedMatrix = cachedMatrix;
        this.spreadSheet = spreadsheet;
        this.workSheetName = worksheetName;
    }

    @Override
    public void run() {
        for (COL col : cachedMatrix.cellMatrix.keySet()) {
            AbstractMap<Integer, String> rows = cachedMatrix.cellMatrix.get(col);
            for (Integer row : rows.keySet()) {
                spreadSheet.insertValueIntoCell(this.workSheetName, col.ordinal() , row, rows.get(row));
            }
        }
    }

    private static void TestFileIdFetch(String arg) throws IOException {
        FileList myFiles = ListSpreadsheets.retrieveAllFiles(arg);
    }

}