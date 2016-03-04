package work.billing.Export;

import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;
import work.billing.Spreadsheets.SpreadsheetFormulas;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.*;

/**
 * Created by mhillbrand on 2/22/2016.
 */
public class ProjectSummarySpreadsheetExporter extends BaseSpreadSheetMatrix {

    public static final String PROJECT_LEAD_NAME = "Felix Schwenk";
    private AbstractMap<String, Integer> hourRatePerTeamMember = new HashMap<>();
    private int lastPosition = 0;
    private int posForSumUp = 0;
    private TrackedTimeSummary trackedTimeSum;


    public ProjectSummarySpreadsheetExporter(TrackedTimeSummary trackedTimeSum, AbstractMap<String, Integer> hourRatePerTeamMember) {
        this.trackedTimeSum = trackedTimeSum;
        this.hourRatePerTeamMember = hourRatePerTeamMember;
    }

    public void createBillingSpreadsheet() {
        List<String> teamMembers = new ArrayList<>();
        teamMembers.addAll(this.hourRatePerTeamMember.keySet());

        //**************************** SECTION TO CREATE INTERNAL PRJ PRESENTATION ***********************
        int startPosInternalProjects = teamMembers.size()+2;

        InternalProjectMatrixListCreator internalPrjMatrix = new InternalProjectMatrixListCreator(trackedTimeSum, PROJECT_LEAD_NAME);
        internalPrjMatrix.initialize(startPosInternalProjects);
        //************************************************************************************************
        //************************** SECTION TO CREATE TEAM MEMBER OVERVIEW ******************************

        TeamMemberOverviewMatrix teamMemberMatrix = new TeamMemberOverviewMatrix(teamMembers);
        teamMemberMatrix.putTeamMembersAndTimesToMatrix(startPosInternalProjects, internalPrjMatrix.getLastPosOfProjects());
        //************************************************************************************************
        //************************** SECTION TO CREATE BILLING PRJ SUMMARY *******************************
        BillingProjectMatrixListCreator billingProjects = new BillingProjectMatrixListCreator(
                internalPrjMatrix.getInternalProjectMatrices(),
                internalPrjMatrix.getFirstPos(),
                internalPrjMatrix.getLastPosOfProjects());
        billingProjects.initialize(internalPrjMatrix.getPrjSummary().getLastPos()+2);

        //************************************************************************************************

        internalPrjMatrix.getPrjSummary().initializeFieldForOverallSummary(billingProjects.getBillingProjectsPositions());
    }
}
