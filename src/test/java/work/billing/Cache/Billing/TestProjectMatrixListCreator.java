package work.billing.Cache.Billing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import work.billing.Cache.MockPrjPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhillbrand on 3/5/2016.
 */
public class TestProjectMatrixListCreator {
    private List<MockPrjPos> mockInternalPrj = new ArrayList<>();
    @Before
    public void setUp() {
        mockInternalPrj.add(new MockPrjPos(10));
        mockInternalPrj.add(new MockPrjPos(20));
        mockInternalPrj.add(new MockPrjPos(30));
        mockInternalPrj.add(new MockPrjPos(40));
    }
    @Test
    public void initializeProjectListCreator() {
        ProjectMatrixListCreator prjListCreator = new ProjectMatrixListCreator(mockInternalPrj, 3, 20);

        prjListCreator.initialize(30);

        Assert.assertEquals(64, prjListCreator.getPrjSummary().getLastPos());
    }
}
