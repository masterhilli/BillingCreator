package work.billing.Spreadsheets;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;
import google.api.auth.AuthorizeService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by mhillbrand on 2/4/2016.
 */
public class Spreadsheet {
    private SpreadsheetEntry googleSpreadSheet = null;

    public Spreadsheet(String googleDriveId) {
        ConnectToSpreadsheetEntry(googleDriveId);
    }

    private void ConnectToSpreadsheetEntry(String googleDriveId) {
        SpreadsheetService service = new SpreadsheetService("MySpreadsheetIntegration-v1");
        try {
            service.setOAuth2Credentials(AuthorizeService.getCredential(Arrays.asList("https://spreadsheets.google.com/feeds"))); //does not find that method????
        } catch (IOException e) {
            e.printStackTrace();
        }

        URL SPREADSHEET_FEED_URL = null;
        try {
            SPREADSHEET_FEED_URL = new URL(
                    //"https://spreadsheets.google.com/feeds/spreadsheets/private/full");
                    "https://spreadsheets.google.com/feeds/worksheets/1MBc1Uvv4Wfyw31mwoGnrEzfCaXxcd1BT-aLg0x1VS_Y/private/full");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = null;
        try {
             feed = service.getFeed(SPREADSHEET_FEED_URL,
                    SpreadsheetFeed.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        List<SpreadsheetEntry> entries = feed.getEntries();

        if (entries.size() > 0) {
            System.out.printf("%d Entries found!\n", entries.size());
        } else {
            System.out.println("no entries have been found");
        }
        Stream<SpreadsheetEntry> entryStream = (entries.stream().filter(e -> e.getKey() == googleDriveId));  // = (SpreadsheetEntry e) -> e.getKey() == googleDriveId;
        SpreadsheetEntry entry = null;
        if (entryStream.count() == 1) {
            entry = entryStream.iterator().next();
            System.out.printf("TITLE: %s\n", entry.getTitle());
        } else {
            System.out.println("I for sure did not find that damn entry");
        }

        // I thought the request brings me the spreadsheet, but it returned me the worksheets ????
        for (SpreadsheetEntry listEntry : entries) {
            System.out.printf("FOUND spreadsheet title: %s\tKey: %s\n", listEntry.getTitle().getPlainText(), listEntry.getKey());
        }
    }

    public boolean IsConnectedToSpreadsheet() {
        return googleSpreadSheet != null;
    }
}
