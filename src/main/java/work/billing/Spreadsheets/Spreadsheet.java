package work.billing.Spreadsheets;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.Pair;
import google.api.GoogleSpreadSheetFeed;
import google.api.GoogleWorksheetHandler;

import java.io.IOException;
import java.util.HashMap;


/**
 * Created by mhillbrand on 2/4/2016.
 */
public class Spreadsheet {
    protected SpreadsheetEntry googleSpreadSheet = null;
    private HashMap<String, Pair<WorksheetEntry, HashMap<String, CellEntry>>> worksheetsContentByWorksheetName =
            new HashMap<>();

    public Spreadsheet(SpreadsheetEntry googleSpreadSheet) {
        this.googleSpreadSheet = googleSpreadSheet;
    }
    public Spreadsheet(String googleDriveFileId) {
        initializeSpreadsheet(googleDriveFileId, "");
    }

    public Spreadsheet(String googleDriveFileId, String worksheetName) {
        initializeSpreadsheet(googleDriveFileId, worksheetName);
    }
    public void update() {
        if (isConnectedToSpreadsheet()) {
            String updateOnlyWorksheetName = "";
            if (worksheetsContentByWorksheetName.size() == 1) {
                for (String key : worksheetsContentByWorksheetName.keySet())
                {
                    updateOnlyWorksheetName = key;
                }
            }
            initializeSpreadsheet(googleSpreadSheet.getKey(), updateOnlyWorksheetName);
        }
    }
    private void initializeSpreadsheet(String googleDriveFileId, String worksheetName) {
        googleSpreadSheet = GoogleSpreadSheetFeed.GetSpreadsheetEntryByKey(googleDriveFileId);
        if (googleSpreadSheet != null) {
            HashMap<String, WorksheetEntry> worksheetsHashMap = GoogleWorksheetHandler.getWorksheetsForSpreadsheetEntry(googleSpreadSheet);
            for (String key :  worksheetsHashMap.keySet()) {
                Pair<WorksheetEntry, HashMap<String, CellEntry>> myPair = null;
                if (worksheetName.length() == 0 || worksheetName.compareTo(key) == 0) {
                    HashMap<String, CellEntry> cellsByKey = GoogleWorksheetHandler.getCellsFromWorksheet(worksheetsHashMap.get(key));
                    myPair = new Pair<>(worksheetsHashMap.get(key), cellsByKey);
                    worksheetsContentByWorksheetName.put(key, myPair);
                }
            }
        }
    }
    public boolean isConnectedToSpreadsheet() {
        return googleSpreadSheet != null;
    }

    @Override
    public String toString() {
        String retVal = "***********************************************************************************\n";
        retVal += String.format("Title: %s (key: %s)\n", googleSpreadSheet.getTitle().getPlainText(),
                googleSpreadSheet.getKey());
        for (Pair<WorksheetEntry, HashMap<String, CellEntry>> entry : worksheetsContentByWorksheetName.values()) {
            retVal += String.format("-WS: %s (R:%d C:%d)\n", entry.first.getTitle().getPlainText(),
                    entry.first.getRowCount(), entry.first.getColCount());
        }
        retVal += "***********************************************************************************";

        return retVal;
    }

    public void addNewWorksheet(String name) {
        GoogleWorksheetHandler.createNewWorksheet(googleSpreadSheet, name);
    }
    public void deleteWorksheet(String name) {
        GoogleWorksheetHandler.deleteWorksheet(worksheetsContentByWorksheetName.get(name).first);
    }
    public void copyWorksheet(String copyFrom, String createNewWorksheet) {
        WorksheetEntry tobeCopied = worksheetsContentByWorksheetName.get(copyFrom).first;
        tobeCopied.setTitle(new PlainTextConstruct(createNewWorksheet));
        GoogleWorksheetHandler.addWorksheetEntry(googleSpreadSheet, tobeCopied);
        update();
        try {
            GoogleWorksheetHandler.copyCellsFromWorksheetToWorksheet(worksheetsContentByWorksheetName.get(copyFrom).first,
                                                                     worksheetsContentByWorksheetName.get(createNewWorksheet).first);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
    public void insertValueIntoCell(String worksheet, String key, String value) {
        WorksheetEntry wsEntry = worksheetsContentByWorksheetName.get(worksheet).first;
        GoogleWorksheetHandler.updateCellValueInWorksheet(wsEntry, key, value);
    }
    public String receiveValueAtKey(String worksheetKey, String cellKey) {
        return worksheetsContentByWorksheetName.get(worksheetKey).second.get(cellKey).getCell().getValue();
    }
}
