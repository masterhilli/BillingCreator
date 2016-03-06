package work;


import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.api.services.drive.model.FileList;
import work.billing.Cache.BaseSpreadSheetMatrix;
import work.billing.Export.BillingExporter;
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

public class AppRunner {

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            switch (args[0]) {
                case "-create":
                    BillingExporter.exportProjectDataToSpreadSheet(args[1], args[2]);
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

    private static void TestFileIdFetch(String arg) throws IOException {
        FileList myFiles = ListSpreadsheets.retrieveAllFiles(arg);
    }

}