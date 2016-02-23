package work.billing.Spreadsheets;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.Pair;
import google.api.GoogleServiceConnector;
import google.api.GoogleSpreadSheetFeed;
import google.api.GoogleWorksheetHandler;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


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
                updateOnlyWorksheetName = worksheetsContentByWorksheetName.keySet().stream().collect(Collectors.toList()).get(0);

            }
            initializeSpreadsheet(googleSpreadSheet.getKey(), updateOnlyWorksheetName);
        }
    }
    public void update(String workSheetName) {
        if (isConnectedToSpreadsheet()) {
            initializeSpreadsheet(googleSpreadSheet.getKey(), workSheetName);
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
    public void insertValueIntoCell(String worksheet, int column, int row, String value) {
        WorksheetEntry wsEntry = worksheetsContentByWorksheetName.get(worksheet).first;
        CellEntry cellEntry = new CellEntry(row, column, value);
        try {
            GoogleServiceConnector.GetSpreadSheetService().insert(wsEntry.getCellFeedUrl(), cellEntry);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        //GoogleWorksheetHandler.updateCellValueInWorksheet(wsEntry, column, row, value);
    }
    public String receiveValueAtKey(String worksheetKey, String cellKey) {
        return worksheetsContentByWorksheetName.get(worksheetKey).second.get(cellKey).getCell().getValue();
    }

    public void getRows(String worksheet) {
        WorksheetEntry wsEntry = worksheetsContentByWorksheetName.get(worksheet).first;

        URL listFeedUrl = wsEntry.getListFeedUrl();
        ListFeed listFeed = null;
        try {
            listFeed = GoogleServiceConnector.GetSpreadSheetService().getFeed(listFeedUrl, ListFeed.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        if (listFeed == null)
            System.out.println("listFeed was null");
        System.out.println("Size of list feed entries: " + listFeed.getEntries().size());

        for (ListEntry row : listFeed.getEntries()) {
            System.out.printf("Row: %s", row.getTitle().getPlainText());
        }
    }

    public void addData(String worksheet) {
        URL listFeedUrl =  worksheetsContentByWorksheetName.get(worksheet).first.getListFeedUrl();
        Map<String,String> rowValues = new HashMap<>();
        rowValues.put("A1", "TEST");
        rowValues.put("A2", "TEST");
        rowValues.put("B1", "TEST");
        rowValues.put("C1", "TEST");
        rowValues.put("D1", "TEST");
        ListEntry row = createRow(rowValues);
        try {
            row = GoogleServiceConnector.GetSpreadSheetService().insert(listFeedUrl, row);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private ListEntry createRow(Map<String, String> rowValues) {
        ListEntry row = new ListEntry();
        for (String columnName : rowValues.keySet()) {
            Object value = rowValues.get(columnName);
            row.getCustomElements().setValueLocal(columnName,
                    String.valueOf(value));
        }
        return row;
    }

}
