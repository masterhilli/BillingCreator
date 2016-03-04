package work.billing.Spreadsheets;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public enum COL {
    NIL("_"),
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G");

    COL(String colName) {
        this.colName = colName;
    }

    private final String colName;

    @Override
    public String toString() {
        return this.colName;
    }
}
