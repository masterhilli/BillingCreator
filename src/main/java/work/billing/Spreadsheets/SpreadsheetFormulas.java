package work.billing.Spreadsheets;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class SpreadsheetFormulas {

    public static String SUM(COL col1, int fromPos, int toPos) {
        return String.format("=SUM(%s%d:%s%d)", col1.toString(), fromPos, col1.toString(), toPos);
    }

    public static String SUMIF(String colValToCheck, String colValToSum,
                               int fromPos, int toPos,
                               String valToCheckAgainst, int valueToCheckPos) {
        return String.format("=SUMIF($%s$%d:$%s$%d,%s%d,$%s$%d:$%s$%d)",
                colValToCheck, fromPos, colValToCheck, toPos,
                valToCheckAgainst, valueToCheckPos,
                colValToSum, fromPos, colValToSum, toPos);
    }

    public static String TIMES(COL col1, int row1, COL col2, int row2) {
        return String.format("=%s%d*%s%d", col1.toString(), row1, col2.toString(), row2);
    }

    public static String PERCENT_OF(String col, int row, double percent) {
        return String.format("=%s%d*%.2f",col, row, percent);
    }


    public static String formatDouble(double value) {
        return String.format("%.2f", value);
    }

    public static String ADD(String col1, int row1, String col2, int row2) {
        return String.format("=%s%d+%s%d", col1, row1, col2, row2);
    }

    public static String ROUND(COL col, int row) {
        return String.format("=ROUND(%s%d,2)", col.toString(), row);
    }
}
