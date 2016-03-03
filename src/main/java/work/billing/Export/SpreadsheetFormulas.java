package work.billing.Export;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class SpreadsheetFormulas {

    public static String SUM(String col, int fromPos, int toPos) {
        return String.format("=SUM(%s%d:%s%d)", col, fromPos, col, toPos);
    }

    public static String SUMIF(String colValToCheck, String colValToSum,
                               int fromPos, int toPos,
                               String valToCheckAgainst, int valueToCheckPos) {
        return String.format("=SUMIF($%s$%d:$%s$%d,%s%d,$%s$%d:$%s$%d)",
                colValToCheck, fromPos, colValToCheck, toPos,
                valToCheckAgainst, valueToCheckPos,
                colValToSum, fromPos, colValToSum, toPos);
    }
}
