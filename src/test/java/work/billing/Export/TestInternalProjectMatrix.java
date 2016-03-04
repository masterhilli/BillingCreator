package work.billing.Export;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import work.billing.Timesheet.TrackedTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public class TestInternalProjectMatrix {
    private InternalProjectMatrix internalPrjMatrix;
    private List<TrackedTime> trackedTimes;
    public static final String PROJECT_NAME = "TestName";

    @BeforeClass
    public void SetUpClass() {
        trackedTimes = new ArrayList<>();
    }
    @Before
    public void Setup() {
        internalPrjMatrix = new InternalProjectMatrix(PROJECT_NAME, trackedTimes);
    }

    @Test
    public void PutHeadingToMatrix_WithStartPos5_ReturnsCorrectHeadingOrder() {
        int row = 5;
        internalPrjMatrix.putHeadingToMatrix(row);

        Assert.assertEquals(internalPrjMatrix.cellMatrix.get(BaseSpreadSheetMatrix.COL.A.ordinal()).get(row), PROJECT_NAME);
        Assert.assertEquals(internalPrjMatrix.cellMatrix.get(BaseSpreadSheetMatrix.COL.C.ordinal()).get(row), I18N.HOUR_RATE);
        Assert.assertEquals(internalPrjMatrix.cellMatrix.get(BaseSpreadSheetMatrix.COL.D.ordinal()).get(row), I18N.NET);
        Assert.assertEquals(internalPrjMatrix.cellMatrix.get(BaseSpreadSheetMatrix.COL.E.ordinal()).get(row), I18N.VAT);
        Assert.assertEquals(internalPrjMatrix.cellMatrix.get(BaseSpreadSheetMatrix.COL.F.ordinal()).get(row), I18N.PRE_TAX);
        Assert.assertEquals(internalPrjMatrix.getHeadingRow(), row);
    }

    @Test
    public void PutHeadingToMatrix_WithStartPos5_ReturnsCorrectColumnSizeOf5() {
        int row = 5;
        internalPrjMatrix.putHeadingToMatrix(row);

        Assert.assertEquals(internalPrjMatrix.cellMatrix.keySet().size(), 5);
    }
}
