package work.billing.Spreadsheets;

import work.billing.Timesheet.TrackedTime;

import java.util.HashMap;

/**
 * Created by mhillbrand on 2/21/2016.
 */
public class ProjectsheetToTrackTimeMapper {
    private static final String PROJECT_KEY = "C4";
    private static final String TEAM_MEMBER_KEY = "C5";
    private static final String MONTH_KEY = "C6";
    private static final String HOURS_KEY = "B8";
    private static final String TRAVEL_COST_KEY = "E8";
    private static final String TRAVEL_HOURS_KEY = "D8";

    public static TrackedTime createTrackedTimeFromSpreadsheet(Spreadsheet timeSheet, String worksheetName, HashMap<String, Integer> hourRatePerTeamMember) {
        TrackedTime trackedTime = new TrackedTime();
        trackedTime.setProjectName(timeSheet.receiveValueAtKey(worksheetName, PROJECT_KEY));
        trackedTime.setTeamMember(timeSheet.receiveValueAtKey(worksheetName, TEAM_MEMBER_KEY));
        trackedTime.setMonthTracked(timeSheet.receiveValueAtKey(worksheetName, MONTH_KEY));
        trackedTime.setHours(Double.parseDouble(timeSheet.receiveValueAtKey(worksheetName, HOURS_KEY)));
        trackedTime.setTravelCosts(Double.parseDouble(timeSheet.receiveValueAtKey(worksheetName, TRAVEL_COST_KEY)));
        trackedTime.setTravelHours(Double.parseDouble(timeSheet.receiveValueAtKey(worksheetName, TRAVEL_HOURS_KEY)));
        trackedTime.setHourRate(hourRatePerTeamMember.get(trackedTime.getTeamMember().toLowerCase()).intValue());
        return trackedTime;
    }
}
