package work.billing.Export;

import work.billing.Cache.BaseSpreadSheetMatrix;
import work.billing.Cache.MonthlyReportCache;
import work.billing.ProjectSpreadSheet.ProjectSpreadsheetToTrackTimeMapper;
import work.billing.Setting.FileSettingReader;
import work.billing.Setting.FileSettings;
import com.masterhilli.google.spreadsheet.api.connector.Spreadsheet;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeAlreadyExistsException;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhillbrand on 3/6/2016.
 */
public class BillingExporter {


    public static void exportProjectDataToSpreadSheet(String worksheetName, String pathToSetting) {
        long startTime = System.currentTimeMillis();

        FileSettings settings = FileSettingReader.ReadFileSettingsFromFile(pathToSetting);
        TrackedTimeSummary trackedTimeSum = getTrackedTimeSummary(worksheetName, settings);

        MonthlyReportCache reportCache = initializeReport(settings, trackedTimeSum);

        List<Thread> threads = startExportOfInternalCacheToSpreadSheet(worksheetName, settings, reportCache);

        waitForExportThreadsToBeFinished(threads);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = (stopTime - startTime)/1000;
        System.out.println("Elapsed time: " + elapsedTime + "s");
    }

    private static TrackedTimeSummary getTrackedTimeSummary(String worksheetName, FileSettings settings) {
        TrackedTimeSummary trackedTimeSum = new TrackedTimeSummary();
        for (String key : settings.importFileId.keySet()) {
            receiveTrackedTimeForSpreadSheetAndAddToTrackedTimeSum(worksheetName, settings, trackedTimeSum, key);
        }
        return trackedTimeSum;
    }

    private static MonthlyReportCache initializeReport(FileSettings settings, TrackedTimeSummary trackedTimeSum) {
        MonthlyReportCache reportCache = new MonthlyReportCache(trackedTimeSum,settings.getHourRateAsHashMapPerTeamMember());

        reportCache.initializeCache();
        return reportCache;
    }

    private static void waitForExportThreadsToBeFinished(List<Thread> threads) {
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
    }

    private static List<Thread> startExportOfInternalCacheToSpreadSheet(String worksheetName, FileSettings settings, MonthlyReportCache reportCache) {
        Spreadsheet spreadsheet = new Spreadsheet(settings.exportFileId, worksheetName);

        List<Thread> threads = new ArrayList<>();
        for (BaseSpreadSheetMatrix cachedMatrix : reportCache.getSpreadSheetMatrixList()) {
            Thread t = new Thread(new BillingExporterThread(cachedMatrix, spreadsheet, worksheetName));
            t.start();
            threads.add(t);

        }
        return threads;
    }

    private static void receiveTrackedTimeForSpreadSheetAndAddToTrackedTimeSum(String worksheetName, FileSettings settings, TrackedTimeSummary trackedTimeSum, String key) {
        Spreadsheet timeSheet = new Spreadsheet(key, worksheetName);
        TrackedTime timeTracked = ProjectSpreadsheetToTrackTimeMapper.createTrackedTimeFromSpreadsheet(
                timeSheet, worksheetName, settings.getHourRateAsHashMapPerTeamMember());
        try {
            trackedTimeSum.addTrackedTime(timeTracked);
        } catch (TrackedTimeAlreadyExistsException e) {
            e.printStackTrace();
        }
    }
}
