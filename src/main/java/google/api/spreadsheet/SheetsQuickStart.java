package google.api.spreadsheet;

import com.google.gdata.client.Query;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import google.api.auth.AuthorizeService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class SheetsQuickStart {

    private static SpreadsheetService service;
    private static URL SPREADSHEET_FEED_URL;

    // Make a request to the API and get all spreadsheets.
    private static SpreadsheetFeed feed;
    private static List<SpreadsheetEntry> spreadsheets;
    private static SpreadsheetEntry spreadsheet = null;
    public static void run(String[] args) {
        try {
            CreateServiceForGoogleSpreadsheets();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            CreateSpreadsheetFeed();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        //FindAllSpreadsheetsInGoogleDrive();
        //RetrieveAdditionalInformationAboutSpreadsheets();
        FindSpreadsheetForTesting();
        //AddWorksheetToTestSpreadsheet();
        //ModifyWorksheetFromTestSpreadsheet();
        //RemoveWorksheetFromTestSpreadsheet();


        try {
            RetrieveListBasedWorksheetFeed();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        try {
            AddARow();  // currently throws exception
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        try {
            RetrieveCellBasedFeeds();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        try {
            RetrieveSpecificRowsColumnsFromCellBasedFeed();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            ChangeContentOfACell();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }



    private static void CreateServiceForGoogleSpreadsheets() throws IOException {
        service = new SpreadsheetService("MySpreadsheetIntegration-v1");
        service.setOAuth2Credentials(AuthorizeService.getCredential(Arrays.asList("https://spreadsheets.google.com/feeds"))); //does not find that method????
    }
    private static void CreateSpreadsheetFeed() throws IOException, ServiceException{
        SPREADSHEET_FEED_URL = new URL(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

        // Make a request to the API and get all spreadsheets.
        feed = service.getFeed(SPREADSHEET_FEED_URL,
                SpreadsheetFeed.class);
        spreadsheets = feed.getEntries();
    }

    /*
    private static void SelectSheetThrougQuery( ) {
        Query query;
        query = new SpreadsheetQuery();
    }*/

    private static void FindAllSpreadsheetsInGoogleDrive() {
        System.out.println("*****************FindAllSpreadsheetsInGoogleDrive*******************");
        // Iterate through all of the spreadsheets returned
        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            //Print the title of this spreadsheet to the screen
            System.out.println(spreadsheet.getKey());
        }
    }
    private static void RetrieveAdditionalInformationAboutSpreadsheets() throws IOException, ServiceException {
        System.out.println("*****************RetrieveAdditionalInformationAboutSpreadsheets*******************");
        if (spreadsheets.size() == 0) {
            // TODO: There were no spreadsheets, act accordingly.
        }

        // TODO: Choose a spreadsheet more intelligently based on your
        // app's needs.
        SpreadsheetEntry spreadsheet = spreadsheets.get(0);
        System.out.println(spreadsheet.getTitle().getPlainText());

        // Make a request to the API to fetch information about all
        // worksheets in the spreadsheet.
        List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();

        // Iterate through each worksheet in the spreadsheet.
        for (WorksheetEntry worksheet : worksheets) {
            // Get the worksheet's title, row count, and column count.
            String title = worksheet.getTitle().getPlainText();
            int rowCount = worksheet.getRowCount();
            int colCount = worksheet.getColCount();

            // Print the fetched information to the screen for this worksheet.
            System.out.println("\t" + title + "- rows:" + rowCount + " cols: " + colCount);
        }
    }
    private static SpreadsheetEntry FindSpreadsheetForTesting()  {

         if (spreadsheet == null) {
             for (SpreadsheetEntry spreadsheetEntry : feed.getEntries()) {
                 if (spreadsheetEntry.getTitle().getPlainText().compareToIgnoreCase("Tabelle TESTGOOGLEAPI") == 0) {
                     spreadsheet = spreadsheetEntry;
                 }
                 //System.out.println("*** " + spreadSheet.getTitle().getPlainText());
             }

             if (spreadsheet != null) {
                 System.out.println("****** FOUND: " + spreadsheet.getTitle().getPlainText());
             } else {
                 System.out.println("****** NOT FOUND :( ***********");
             }
         }
        return spreadsheet;
    }
    private static void AddWorksheetToTestSpreadsheet() throws IOException, ServiceException {
        SpreadsheetEntry spreadsheet = FindSpreadsheetForTesting();
                // Create a local representation of the new worksheet.
        WorksheetEntry worksheet = new WorksheetEntry();
        worksheet.setTitle(new PlainTextConstruct("New Worksheet"));
        worksheet.setColCount(10);
        worksheet.setRowCount(20);

        // Send the local representation of the worksheet to the API for
        // creation.  The URL to use here is the worksheet feed URL of our
        // spreadsheet.
        URL worksheetFeedUrl = spreadsheet.getWorksheetFeedUrl();
        service.insert(worksheetFeedUrl, worksheet);
    }
    private static void RemoveWorksheetFromTestSpreadsheet() throws IOException, ServiceException {
        SpreadsheetEntry spreadsheet = FindSpreadsheetForTesting();
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Delete the worksheet via the API.
        worksheet.delete();
    }
    private static void ModifyWorksheetFromTestSpreadsheet() throws IOException, ServiceException {
        SpreadsheetEntry spreadsheet = FindSpreadsheetForTesting();
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Update the local representation of the worksheet.
        worksheet.setTitle(new PlainTextConstruct("Updated Worksheet"));
        worksheet.setColCount(5);
        worksheet.setRowCount(15);

        // Send the local representation of the worksheet to the API for
        // modification.
        worksheet.update();
    }
    private static void RetrieveListBasedWorksheetFeed() throws IOException, ServiceException {
        spreadsheet = FindSpreadsheetForTesting();
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Fetch the list feed of the worksheet.
        URL listFeedUrl = worksheet.getListFeedUrl();
        ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

        // Iterate through each row, printing its cell values.
        for (ListEntry row : listFeed.getEntries()) {
            // Print the first column's cell value
            System.out.print(row.getTitle().getPlainText() + "\t");
            // Iterate over the remaining columns, and print each cell value
            for (String tag : row.getCustomElements().getTags()) {
                System.out.print(row.getCustomElements().getValue(tag) + "\t");
            }
            System.out.println();
        }
    }
    private static void AddARow() throws IOException, ServiceException {
        spreadsheet = FindSpreadsheetForTesting();
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Fetch the list feed of the worksheet.
        URL listFeedUrl = worksheet.getListFeedUrl();
        ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

        // Create a local representation of the new row.
        ListEntry row = new ListEntry();
        row.getCustomElements().setValueLocal("firstname", "Joe");
        row.getCustomElements().setValueLocal("lastname", "Smith");
        row.getCustomElements().setValueLocal("age", "26");
        row.getCustomElements().setValueLocal("height", "176");

        // Send the new row to the API for insertion.
        row = service.insert(listFeedUrl, row);  // this one is throwing an exception!
    }
    private static void UpdateARow() throws IOException, ServiceException {
        spreadsheet = FindSpreadsheetForTesting();
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Fetch the list feed of the worksheet.
        URL listFeedUrl = worksheet.getListFeedUrl();
        ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

        // TODO: Choose a row more intelligently based on your app's needs.
        ListEntry row = listFeed.getEntries().get(0);

        // Update the row's data.
        row.getCustomElements().setValueLocal("firstname", "Sarah");
        row.getCustomElements().setValueLocal("lastname", "Hunt");
        row.getCustomElements().setValueLocal("age", "32");
        row.getCustomElements().setValueLocal("height", "154");

        // Save the row using the API.
        row.update();
    }
    private static void DeleteARow() throws IOException, ServiceException {
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Fetch the list feed of the worksheet.
        URL listFeedUrl = worksheet.getListFeedUrl();
        ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

        // TODO: Choose a row more intelligently based on your app's needs.
        ListEntry row = listFeed.getEntries().get(0);

        // Delete the row using the API.
        row.delete();
    }
    private static void RetrieveCellBasedFeeds() throws IOException, ServiceException {
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Fetch the cell feed of the worksheet.
        URL cellFeedUrl = worksheet.getCellFeedUrl();
        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

        // Iterate through each cell, printing its value.
        for (CellEntry cell : cellFeed.getEntries()) {
            // Print the cell's address in A1 notation
            System.out.print(cell.getTitle().getPlainText() + "\t");
            // Print the cell's address in R1C1 notation
            System.out.print(cell.getId().substring(cell.getId().lastIndexOf('/') + 1) + "\t");
            // Print the cell's formula or text value
            System.out.print(cell.getCell().getInputValue() + "\t");
            // Print the cell's calculated value if the cell's value is numeric
            // Prints empty string if cell's value is not numeric
            System.out.print(cell.getCell().getNumericValue() + "\t");
            // Print the cell's displayed value (useful if the cell has a formula)
            System.out.println(cell.getCell().getValue() + "\t");
        }
    }
    private static void RetrieveSpecificRowsColumnsFromCellBasedFeed() throws IOException, ServiceException, URISyntaxException {
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Fetch column 4, and every row after row 1.
        URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
                + "?min-row=2&min-col=4&max-col=4").toURL();
        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

        // Iterate through each cell, printing its value.
        for (CellEntry cell : cellFeed.getEntries()) {
            // Print the cell's address in A1 notation
            System.out.print(cell.getTitle().getPlainText() + "\t");
            // Print the cell's address in R1C1 notation
            System.out.print(cell.getId().substring(cell.getId().lastIndexOf('/') + 1) + "\t");
            // Print the cell's formula or text value
            System.out.print(cell.getCell().getInputValue() + "\t");
            // Print the cell's calculated value if the cell's value is numeric
            // Prints empty string if cell's value is not numeric
            System.out.print(cell.getCell().getNumericValue() + "\t");
            // Print the cell's displayed value (useful if the cell has a formula)
            System.out.println(cell.getCell().getValue() + "\t");
        }
    }

    private static void ChangeContentOfACell() throws IOException, ServiceException {
        // Get the first worksheet of the first spreadsheet.
        // TODO: Choose a worksheet more intelligently based on your
        // app's needs.
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        WorksheetEntry worksheet = worksheets.get(0);

        // Fetch the cell feed of the worksheet.
        URL cellFeedUrl = worksheet.getCellFeedUrl();
        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

        // Iterate through each cell, updating its value if necessary.
        // TODO: Update cell values more intelligently.
        for (CellEntry cell : cellFeed.getEntries()) {
            if (cell.getTitle().getPlainText().equals("A1")) {
                cell.changeInputValueLocal("200");
                cell.update();
            } else if (cell.getTitle().getPlainText().equals("B1")) {
                cell.changeInputValueLocal("=SUM(A1; 200)");
                cell.update();
            }
        }
    }
}