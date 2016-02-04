package work;

import google.api.spreadsheet.SheetsQuickStart;
import work.billing.Spreadsheets.Spreadsheet;

import java.io.IOException;

public class AppRunner {
    public static void main(String[] args) throws IOException {
        //google.api.drive.DriveQuickstart.run(args);
        //SheetsQuickStart.run(args);
        Spreadsheet spreadsheet = new Spreadsheet("1MBc1Uvv4Wfyw31mwoGnrEzfCaXxcd1BT-aLg0x1VS_Y");
    }

}