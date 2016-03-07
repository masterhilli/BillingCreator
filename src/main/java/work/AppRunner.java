package work;


import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import work.billing.Export.BillingExporter;
import work.billing.Files.ListSpreadsheets;
import work.billing.Setting.FileSettingReader;
import work.billing.Setting.FileSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppRunner {

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            switch (args[0]) {
                case "-create":
                    BillingExporter.exportProjectDataToSpreadSheet(args[1], args[2]);
                    break;
                case "-list":
                    ReceiveFileListAndExportToFile(args[1]);
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
        System.out.println("-list <filterstring> <fileToExport>");
    }

    private static void ReceiveFileListAndExportToFile(String fileNameToExport) throws IOException {
        FileSettings fileSettings = FileSettingReader.ReadFileSettingsFromFile(fileNameToExport);

        List<File> spreadSheetFileList = new ArrayList<>();
        for (String searchParam : fileSettings.searchParams) {
            FileList myFiles = ListSpreadsheets.retrieveAllFiles(searchParam);
            spreadSheetFileList.addAll(myFiles.getFiles());
        }

        reCreateSettingsFile(fileNameToExport);
        addNewFilesToImportList(spreadSheetFileList, fileSettings);

        FileSettingReader.WriteFileSettingsToFile(fileNameToExport, fileSettings);
    }

    private static void addNewFilesToImportList(List<File> spreadSheetFiles, FileSettings fileSettings) {
        if (fileSettings.importFileId == null) {
            fileSettings.importFileId = new HashMap<String, String>();
        }
        fileSettings.importFileId.clear();
        for (File file : spreadSheetFiles) {
            fileSettings.importFileId.put(file.getId(), file.getName());
        }
    }

    private static void reCreateSettingsFile(String fileNameToExport) throws IOException {
        Path pathToNewFile = Paths.get(fileNameToExport);
        if (Files.exists(pathToNewFile, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(pathToNewFile);
        }
        Files.createFile(pathToNewFile);
    }

}