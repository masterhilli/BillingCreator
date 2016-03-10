package work.billing.Export;

import work.billing.Cache.BaseSpreadSheetMatrix;
import work.billing.Spreadsheets.COL;
import com.masterhilli.google.spreadsheet.api.connector.Spreadsheet;

import java.util.AbstractMap;

/**
 * Created by mhillbrand on 3/6/2016.
 */
public class BillingExporterThread implements Runnable {
    private final String workSheetName;
    private final Spreadsheet spreadsheet;
    private final BaseSpreadSheetMatrix cachedMatrix;

    public BillingExporterThread(BaseSpreadSheetMatrix cachedMatrix, Spreadsheet spreadsheet, String worksheetName) {
        this.cachedMatrix = cachedMatrix;
        this.spreadsheet = spreadsheet;
        this.workSheetName = worksheetName;
    }

    @Override
    public void run() {
        for (COL col : cachedMatrix.cellMatrix.keySet()) {
            AbstractMap<Integer, String> rows = cachedMatrix.cellMatrix.get(col);
            for (Integer row : rows.keySet()) {
                spreadsheet.insertValueIntoCell(this.workSheetName, col.ordinal() , row, rows.get(row));
            }
        }
    }
}
