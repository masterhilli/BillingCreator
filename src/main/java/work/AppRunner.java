package work;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import google.api.GoogleServiceConnector;
import google.api.GoogleSpreadSheetFeed;
import google.api.spreadsheet.SheetsQuickStart;
import work.billing.Spreadsheets.Spreadsheet;

import java.io.IOException;

public class AppRunner {

    private static String[] sheetsToReceiveInformationFrom = {  "1PcKhhiCfshJoudCfnpNobSpleOTzRcZPPYbVboNVrSE",
            "1UtHN8gdm52ivZavaIuyzsBKO3qKcOobaHEzSenRBYu4",
            "1kc-Ofekt_CxysmDfQgXt6L3-wz5xIyv8VifUoryrS9E",
            "1CI_-8751llyHDucjjKExtSmq3vmLHjo8-Yt5CE1pdSA",
            "1fSlXhu2T1gUrUBRWXgu-uEdmHY-toW7pBCErZi35RNc",
            "1_W2mb2wO9S8fTjv17oqf66TAqJ3ibv1AMtEOMNYmJiU",
            "1pfGiE4YSX2p3_GU3S0pKYZrzNLYDyu2pEAQd7k-AqI8"};
    public static void main(String[] args) throws IOException {
        for (String key : sheetsToReceiveInformationFrom) {
            PrintValuesOfTimesheets(key, "2016-01");
        }

        TestMethodsForSpreadSheets();
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

    private static final String PROJECT_KEY = "C4";
    private static final String TEAMMEMBER_KEY = "C5";
    private static final String MONTH_KEY = "C6";
    private static final String HOURS_KEY = "B8";
    private static final String TRAVEL_COST_KEY = "E8";
    private static final String TRAVEL_HOURS_KEY = "D8";

    public static void PrintValuesOfTimesheets(String googleDriveFileId, String worksheetName) {
        Spreadsheet timesheet = new Spreadsheet(googleDriveFileId, worksheetName);
        String project = timesheet.receiveValueAtKey(worksheetName, PROJECT_KEY);
        String teammember = timesheet.receiveValueAtKey(worksheetName, TEAMMEMBER_KEY);
        String month = timesheet.receiveValueAtKey(worksheetName, MONTH_KEY);
        String hours = timesheet.receiveValueAtKey(worksheetName, HOURS_KEY);
        String travelCosts = timesheet.receiveValueAtKey(worksheetName, TRAVEL_COST_KEY);
        String travelHours = timesheet.receiveValueAtKey(worksheetName, TRAVEL_HOURS_KEY);
        String formatString = "************************************************************************"+
                              "\nprj: %s teamm: %s month: %s hours: %s tCost: %s tHours: %s\n" +
                              "************************************************************************\n";
        System.out.printf(formatString, project, teammember, month, hours, travelCosts, travelHours);
    }

}