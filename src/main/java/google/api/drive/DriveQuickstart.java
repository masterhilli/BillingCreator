package google.api.drive;

import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.*;
import java.util.List;

import google.api.auth.AuthorizeService;

public class DriveQuickstart {

    public static void run(String[] args) throws IOException {
        // Build a new authorized API client service.
        Drive driveService = AuthorizeService.getDriveService(null);

        // Print the names and IDs for up to 10 files.
        FileList result = driveService.files().list()
             .setQ("name contains \"TESTGOOGLEAPI\"")
             .setFields("nextPageToken, files(id, name)")
             .execute();
        List<File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }

        String fileId;
        OutputStream outputStream;
        /*try {
            // download a file:
            fileId = "0B6t3DlGVKk9bajZhREh0d2JEWkk";
            outputStream = new ByteArrayOutputStream();
            driveService.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
            System.out.println(outputStream.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } */
        try {
            Thread.sleep(500);
        } catch (Exception ex) {}

        try {
            System.out.println("STARTING GETTING THE EXCEL FILE:");
            fileId = "1MBc1Uvv4Wfyw31mwoGnrEzfCaXxcd1BT-aLg0x1VS_Y";
            outputStream = new ByteArrayOutputStream();
            driveService.files().export(fileId, "text/csv")
                    .executeMediaAndDownloadTo(outputStream);
            System.out.println(outputStream.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        try {
            System.out.println("STARTING GETTING THE EXCEL FILE: as openoffice");
            fileId = "1MBc1Uvv4Wfyw31mwoGnrEzfCaXxcd1BT-aLg0x1VS_Y";
            outputStream = new ByteArrayOutputStream();
            driveService.files().export(fileId, "application/x-vnd.oasis.opendocument.spreadsheet")
                    .executeMediaAndDownloadTo(outputStream);
            System.out.println(outputStream.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        try {
            System.out.println("STARTING GETTING THE DOC FILE: as pdf");
            fileId = "1Gwm8e_BKShRqHXaOiAmG0bpeltpwVamU3WgKRncbmPw";
            outputStream = new ByteArrayOutputStream();
            driveService.files().export(fileId, "text/plain")
                    .executeMediaAndDownloadTo(outputStream);
            System.out.println(outputStream.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

}