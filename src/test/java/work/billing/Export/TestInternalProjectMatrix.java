package work.billing.Export;

import junit.framework.Assert;
import org.junit.Test;
import work.billing.Timesheet.TrackedTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public class TestInternalProjectMatrix {

    public static final String PROJECT_NAME = "TestName";

    @Test
    public void PutHeadingToMatrix_WithStartPos5_ReturnsCorrectHeadingOrder() {
        InternalProjectMatrix internalPrjMatrix = new InternalProjectMatrix(PROJECT_NAME, new ArrayList<TrackedTime>());

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
        InternalProjectMatrix internalPrjMatrix = new InternalProjectMatrix(PROJECT_NAME, new ArrayList<TrackedTime>());

        int row = 5;
        internalPrjMatrix.putHeadingToMatrix(row);

        Assert.assertEquals(internalPrjMatrix.cellMatrix.keySet().size(), 5);
    }
}
