package work.billing.Timesheet;

/**
 * Created by mhillbrand on 2/15/2016.
 */
public class WorkLog {
    private String projectName;
    private String teammember;
    private double hours;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTeammember() {
        return teammember;
    }

    public void setTeammember(String teammember) {
        this.teammember = teammember;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }
}
