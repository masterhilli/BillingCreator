package work.billing.Files;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import google.api.auth.AuthorizeService;

import java.io.IOException;
import java.util.Arrays;

import static google.api.auth.AuthorizeService.getDriveService;

/**
 * Created by felix on 23/02/2016.
 */
public class ListSpreadsheets {
    public static FileList retrieveAllFiles(String arg) throws IOException{
        Credential authCredential = AuthorizeService.getCredential(Arrays.asList("https://spreadsheets.google.com/feeds"));
        Drive driveService = getDriveService(authCredential);

        FileList result;
        String pageToken = null;

        do {
             result = driveService.files().list()
                    .setQ("(mimeType='application/vnd.google-apps.spreadsheet') and (name contains '" + arg + "')")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            for(File file: result.getFiles()) {
                System.out.printf("%s;%s\n",
                        file.getName(), file.getId());
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return result;
    }
}
