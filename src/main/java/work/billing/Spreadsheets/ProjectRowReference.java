package work.billing.Spreadsheets;

/**
 * Created by mhillbrand on 2/23/2016.
 */
public class ProjectRowReference {
    private int sumRow;
    private int travelCostRow;
    private int projectNameRow;
    private int sumRowFor2ndRun;
    private int projectNameRow2nd;

    public void setSumRow(int sumRow) {
        this.sumRow = sumRow;
    }

    public void setTravelCostRow(int travelCostRow) {
        this.travelCostRow = travelCostRow;
    }

    public void setProjectNameRow(int projectNameRow) {
        this.projectNameRow = projectNameRow;
    }

    public int getSumRow() {
        return sumRow;
    }

    public int getTravelCostRow() {
        return travelCostRow;
    }

    public int getProjectNameRow() {
        return projectNameRow;
    }

    public void setSumRowFor2ndRun(int sumRowFor2ndRun) {
        this.sumRowFor2ndRun = sumRowFor2ndRun;
    }

    public int getSumRowFor2ndRun() {
        return sumRowFor2ndRun;
    }

    public void setProjectNameRow2nd(int projectNameRow2nd) {
        this.projectNameRow2nd = projectNameRow2nd;
    }

    public int getProjectNameRow2nd() {
        return projectNameRow2nd;
    }
}
