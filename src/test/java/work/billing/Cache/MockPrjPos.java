package work.billing.Cache;

/**
 * Created by mhillbrand on 3/5/2016.
 */
public class MockPrjPos implements ProjectPositions{
    private final int sumRow;

    public MockPrjPos(int sumRow) {
        this.sumRow = sumRow;
    }

    @Override
    public int getHeadingRow() {
        return sumRow - 4;
    }

    @Override
    public int getSumRow() {
        return sumRow;
    }

    @Override
    public int getTravelCostRow() {
        return sumRow - 1;
    }
}
