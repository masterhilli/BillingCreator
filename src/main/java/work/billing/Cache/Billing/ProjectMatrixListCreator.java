package work.billing.Cache.Billing;

import work.billing.Cache.ProjectPositions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhillbrand on 3/4/2016.
 */
public class ProjectMatrixListCreator {
    private final int firstInternalPrjRow;
    private final int lastInternalPrjRow;
    private List<? extends ProjectPositions> internalPrjPositions;
    private List<ProjectMatrix> billingProjectMatrices;
    ProjectSummary billingSummary;

    public List<ProjectMatrix> getProjectMatrices() {
        return billingProjectMatrices;
    }

    public ProjectSummary getPrjSummary() {
        return billingSummary;
    }

    public ProjectMatrixListCreator(List<? extends ProjectPositions> internalPrjPositions, int firstRow, int lastRow) {
        this.internalPrjPositions = internalPrjPositions;
        this.billingProjectMatrices = new ArrayList<ProjectMatrix>();
        this.firstInternalPrjRow = firstRow;
        this.lastInternalPrjRow = lastRow;
    }

    public void initialize(int startPos) {
        startPos = initializeProjectsToMatrix(startPos);
        initializeProjectSummary(++startPos);
    }

    private int initializeProjectsToMatrix(int startPos) {
        for (ProjectPositions prjPos : internalPrjPositions) {
            ProjectMatrix billingPrjMatrix = new ProjectMatrix(prjPos);
            billingPrjMatrix.initialize(startPos);
            startPos = billingPrjMatrix.getSumRow() + 2;
            billingProjectMatrices.add(billingPrjMatrix);
        }
        return startPos;
    }

    protected void initializeProjectSummary(int startPos) {
        billingSummary = new ProjectSummary(billingProjectMatrices, firstInternalPrjRow, lastInternalPrjRow);
        billingSummary.initialize(startPos);

    }
}
