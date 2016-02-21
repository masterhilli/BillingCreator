package work.billing.Timesheet;

/**
 * Created by mhillbrand on 2/15/2016.
 */
public class TrackedTime {
    private String projectName;
    private String monthTracked;
    private String teamMember;
    private double hours;
    private double travelHours;
    private double travelCosts;
    private int hourRate;

    public TrackedTime() {}
    public TrackedTime(String projectName, String monthTracked, String teamMember, double hours,
                       double travelCosts, double travelHours, int hourRate) {
        setProjectName(projectName);
        setMonthTracked(monthTracked);
        setTeamMember(teamMember);
        setHours(hours);
        setTravelCosts(travelCosts);
        setTravelHours(travelHours);
        setHourRate(hourRate);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(String teamMember) {
        this.teamMember = teamMember;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getTravelHours() {
        return travelHours;
    }

    public void setTravelHours(double travelHours) {
        this.travelHours = travelHours;
    }

    public double getTravelCosts() {
        return travelCosts;
    }

    public void setTravelCosts(double travelcosts) {
        this.travelCosts = travelcosts;
    }

    public int getHourRate() {
        return hourRate;
    }

    public void setHourRate(int hourRate) {
        this.hourRate = hourRate;
    }

    public String getMonthTracked() {
        return monthTracked;
    }

    public void setMonthTracked(String monthTracked) {
        this.monthTracked = monthTracked;
    }

    @Override
    public String toString() {
        String formatString = "Prj:(%s) Teamm:(%s) Month:(%s) Hours:(%.2f) Cost:(%.2f) Hours:(%.2f) Rate:(%d)";
        String output = String.format(formatString, this.getProjectName(),
                this.getTeamMember(), this.getMonthTracked(), this.getHours(),
                this.getTravelCosts(), this.getTravelHours(), this.getHourRate());
        return output;
    }
}
