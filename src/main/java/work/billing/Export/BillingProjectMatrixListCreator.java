package work.billing.Export;

import work.billing.I18N.I18N;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public class BillingProjectMatrixListCreator {
    private final int firstInternalPrjRow;
    private final int lastInternalPrjRow;
    private List<? extends ProjectPositions> internalPrjPositions;
    private List<BillingProjectMatrix> billingProjectMatrices;
    BillingProjectSummary billingSummary;

    public List<? extends ProjectPositions> getBillingProjectsPositions() {
        return billingProjectMatrices;
    }

    public BillingProjectMatrixListCreator(List<? extends ProjectPositions> internalPrjPositions, int firstRow, int lastRow) {
        this.internalPrjPositions = internalPrjPositions;
        this.billingProjectMatrices = new ArrayList<BillingProjectMatrix>();
        this.firstInternalPrjRow = firstRow;
        this.lastInternalPrjRow = lastRow;
    }

    public void initialize(int startPos) {
        startPos = initializeProjectsToMatrix(startPos);
        initializeProjectSummary(++startPos);
    }

    private int initializeProjectsToMatrix(int startPos) {
        for (ProjectPositions prjPos : internalPrjPositions) {
            BillingProjectMatrix billingPrjMatrix = new BillingProjectMatrix(prjPos);
            billingPrjMatrix.initialize(startPos);
            startPos = billingPrjMatrix.getSumRow() + 2;
            billingProjectMatrices.add(billingPrjMatrix);
        }
        return startPos;
    }

    protected void initializeProjectSummary(int startPos) {
        billingSummary.initialize(startPos);
        billingSummary = new BillingProjectSummary(billingProjectMatrices, firstInternalPrjRow, lastInternalPrjRow);
    }
}
