package work.billing.Export;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeAlreadyExistsException;
import work.billing.Timesheet.TrackedTimeSummary;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public class TestInternalProjectsListingMatrix {
    private static TrackedTimeSummary trackedTimeSum;
    private static final String PROJECT_NAME_1 = "MyProject 1";
    private static final String PROJECT_NAME_2 = "MyProject 2";
    private static final String PROJECT_NAME_3 = "MyProject 3";
    private static final String PROJECT_NAME_4 = "MyProject 4";

    private static final String WORKSHEET = "2016-01";


    private static final String USER_1 = "Martin Hillbrand";
    private static final String USER_2 = "Test User 2";
    private static final String USER_3 = "Test User 3";
    private static final String USER_4 = "Test User 4";
    private static final String USER_5 = "Test User 5";

    @BeforeClass
    public static void SetUpClass() {
        trackedTimeSum = new TrackedTimeSummary();
        try {
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_1, WORKSHEET, USER_1, 5.5, 10.5, 55.9, 90));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_1, WORKSHEET, USER_2, 6.5, 16.5, 56.9, 60));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_1, WORKSHEET, USER_3, 7.5, 17.5, 57.9, 70));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_2, WORKSHEET, USER_1, 5.5, 10.5, 55.9, 90));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_2, WORKSHEET, USER_2, 6.5, 16.5, 56.9, 60));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_3, WORKSHEET, USER_3, 7.5, 17.5, 57.9, 70));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_3, WORKSHEET, USER_1, 5.5, 10.5, 55.9, 90));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_3, WORKSHEET, USER_2, 6.5, 16.5, 56.9, 60));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_1, WORKSHEET, USER_4, 7.5, 17.5, 57.9, 70));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_3, WORKSHEET, USER_5, 5.5, 10.5, 55.9, 90));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_4, WORKSHEET, USER_2, 6.5, 16.5, 56.9, 60));
            trackedTimeSum.addTrackedTime(new TrackedTime(PROJECT_NAME_4, WORKSHEET, USER_3, 7.5, 17.5, 57.9, 70));
        } catch (TrackedTimeAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initializeMatrix_WithStartPos5And3TrackedTimes_ReturnsLastPositionAt28() {
        InternalProjectsListingMatrix internalPrjMatrixLists = new InternalProjectsListingMatrix(trackedTimeSum);

        internalPrjMatrixLists.initializeMatrix(0);

        Assert.assertEquals(28, internalPrjMatrixLists.getLastPos());
    }

    @Test
    public void getInternalProjectMatrices_With4Projects_ReturnsAll4Projects() {
        InternalProjectsListingMatrix internalPrjMatrixLists = new InternalProjectsListingMatrix(trackedTimeSum);

        internalPrjMatrixLists.initializeMatrix(0);

        Assert.assertEquals(4, internalPrjMatrixLists.getInternalProjectMatrices().size());
    }
}
