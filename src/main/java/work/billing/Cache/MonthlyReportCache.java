package work.billing.Cache;

import work.billing.Cache.Internal.ProjectMatrixListCreator;
import work.billing.Cache.Team.TeamMemberOverviewMatrix;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.*;

/**
 * Created by mhillbrand on 2/22/2016.
 */
public class MonthlyReportCache {

    public static final String PROJECT_LEAD_NAME = "Felix Schwenk";
    private AbstractMap<String, Integer> hourRatePerTeamMember = new HashMap<>();
    private TrackedTimeSummary trackedTimeSum;
    private List<BaseSpreadSheetMatrix> spreadSheetMatrixItems;


    public MonthlyReportCache(TrackedTimeSummary trackedTimeSum, AbstractMap<String, Integer> hourRatePerTeamMember) {
        this.trackedTimeSum = trackedTimeSum;
        this.hourRatePerTeamMember = hourRatePerTeamMember;
        this.spreadSheetMatrixItems = new ArrayList<>();
    }

    public void initializeCache() {
        List<String> teamMembers = new ArrayList<>();
        teamMembers.addAll(this.hourRatePerTeamMember.keySet());
        //**************************** SECTION TO CREATE INTERNAL PRJ PRESENTATION ***********************
        int startPosInternalProjects = teamMembers.size()+2;

        ProjectMatrixListCreator internalPrjMatrix = new ProjectMatrixListCreator(trackedTimeSum, PROJECT_LEAD_NAME);
        internalPrjMatrix.initialize(startPosInternalProjects);
        //************************************************************************************************
        //************************** SECTION TO CREATE TEAM MEMBER OVERVIEW ******************************

        TeamMemberOverviewMatrix teamMemberMatrix = new TeamMemberOverviewMatrix(teamMembers);
        teamMemberMatrix.putTeamMembersAndTimesToMatrix(startPosInternalProjects, internalPrjMatrix.getLastPosOfProjects());
        //************************************************************************************************
        //************************** SECTION TO CREATE BILLING PRJ SUMMARY *******************************
        work.billing.Cache.Billing.ProjectMatrixListCreator billingProjects = new work.billing.Cache.Billing.ProjectMatrixListCreator(
                internalPrjMatrix.getProjectMatrices(),
                internalPrjMatrix.getFirstPos(),
                internalPrjMatrix.getLastPosOfProjects());
        billingProjects.initialize(internalPrjMatrix.getPrjSummary().getLastPos()+2);

        //************************************************************************************************
        internalPrjMatrix.getPrjSummary().initializeFieldForOverallSummary(billingProjects.getProjectMatrices());
        spreadSheetMatrixItems.add(teamMemberMatrix);
        spreadSheetMatrixItems.add(internalPrjMatrix.getPrjSummary());
        spreadSheetMatrixItems.addAll(internalPrjMatrix.getProjectMatrices());
        spreadSheetMatrixItems.addAll(billingProjects.getProjectMatrices());
        spreadSheetMatrixItems.add(billingProjects.getPrjSummary());
    }

    public List<BaseSpreadSheetMatrix> getSpreadSheetMatrixList() {
        return spreadSheetMatrixItems;
    }
}
