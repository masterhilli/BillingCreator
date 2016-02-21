package work.billing.Timesheet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Created by mhillbrand on 2/21/2016.
 */
public class TestTrackedTimeSummary {
    private static TrackedTimeSummary trackedTimeSummary;
    private static final String PROJ_HOLZ = "0000 Schnecke Holz";
    private static final String PROJ_ZIPF = "00ss0 Schnecke Zipfer";
    private static final String PROJ_STERN = "2514 Schnecke Sternchen";

    private static final String MONTH_01 = "2016-01";

    private static final String MEM_MARTIN = "Martin Hillbrand";
    private static final String MEM_TEST1 = "Test User";
    private static final String MEM_ANOTHER = "Test AnotherUser";
    @BeforeClass
    public static void initializeTestHelpers() {
        trackedTimeSummary = new TrackedTimeSummary();
        try {
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_HOLZ, MONTH_01, MEM_MARTIN, 15.2, 0.0, 0.0, 90));
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_HOLZ, MONTH_01, MEM_TEST1, 56.2, 0.0, 0.0, 122));
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_HOLZ, MONTH_01, MEM_ANOTHER, 54.2, 0.0, 0.0, 60));
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_ZIPF, MONTH_01, MEM_MARTIN, 23.2, 0.0, 0.0, 110));
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_ZIPF, MONTH_01, MEM_TEST1, 45.2, 0.0, 0.0, 55));
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_ZIPF, MONTH_01, MEM_ANOTHER, 56.2, 0.0, 0.0, 75));
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_STERN, MONTH_01, MEM_MARTIN, 45.2, 0.0, 0.0, 66));
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_STERN, MONTH_01, MEM_TEST1, 99.2, 0.0, 0.0, 110));
        } catch (TrackedTimeAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestAddTrackedTimeThatAlreadyExistsThrowsAlreadyExistsException() {
        TrackedTimeAlreadyExistsException exception = null;
        try {
            trackedTimeSummary.addTrackedTime(new TrackedTime(PROJ_HOLZ, MONTH_01, MEM_MARTIN, 0.0, 0.0, 0.0, 0));
        } catch (TrackedTimeAlreadyExistsException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @Test
    public void TestReceiveTimeTrackedListForHOLZUpperCaseReturns3TrackedTimes() {
        List<TrackedTime> trackedTimesForHOLZ = trackedTimeSummary.receiveTrackedTimesPerProject(PROJ_HOLZ.toUpperCase());
        Assert.assertEquals(trackedTimesForHOLZ.size(), 3);
    }

    @Test
    public void TestReceiveTimeTrackedListForNonProjectReturnsNull() {
        List<TrackedTime> trackedTimesForAny = trackedTimeSummary.receiveTrackedTimesPerProject("ANYTHING");
        Assert.assertNull(trackedTimesForAny);
    }

    @Test
    public void TestReceiveTimeTrackedListForMemMartinReturns3TrackedTimes() {
        List<TrackedTime> trackedTimesForMartin = trackedTimeSummary.receiveTrackedTimesPerTeamMember(MEM_MARTIN);
        Assert.assertEquals(trackedTimesForMartin.size(), 3);
    }

    @Test
    public void TestReceiveTimeTrackedListForMemFritzReturnsNull() {
        List<TrackedTime> trackedTimesForFritz = trackedTimeSummary.receiveTrackedTimesPerTeamMember("Fritz");
        Assert.assertNull(trackedTimesForFritz);
    }

    @Test
    public void TestPrintPerProjectWritesToConsole() {
        trackedTimeSummary.printTimesForAllProjects();
    }

    @Test
    public void TestPrintPerTeamMemberWritesToConsole() {
        trackedTimeSummary.printTimesForAllTeamMembers();
    }
}
