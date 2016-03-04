package work.billing.Export;

import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class InternalProjectsListingMatrix {
    private final HashMap<String, List<TrackedTime>> timesPerProject;

    public List<InternalProjectMatrix> getInternalProjectMatrices() {
        return internalProjectMatrices;
    }

    private List<InternalProjectMatrix> internalProjectMatrices;
    private int lastPos = 0;

    public int getLastPos() {
        return lastPos;
    }

    public InternalProjectsListingMatrix(TrackedTimeSummary trackedTimeSum) {
        this.timesPerProject = trackedTimeSum.getProjectTimesMap();
        this.internalProjectMatrices = new ArrayList<>();
    }

    public void initializeMatrix(int startPos) {
        for (String project : timesPerProject.keySet()) {
            InternalProjectMatrix prjMatrix = new InternalProjectMatrix(project, timesPerProject.get(project));
            prjMatrix.initializeMatrix(startPos);
            this.internalProjectMatrices.add(prjMatrix);
            startPos = prjMatrix.getSumRow()+2;
        }
        lastPos = startPos;
    }

    public List<ProjectRowReference> getRowReferences() {
        return new ArrayList<>(); // TODO: remove that
    }
}
