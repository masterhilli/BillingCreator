package work.billing.Export;

import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class InternalProjectsListingMatrix {
    private final HashMap<String, List<TrackedTime>> timesPerProject;
    private AbstractList<InternalProjectMatrix> internalProjectMatrices;

    public int getLastPos() {
        return lastPos;
    }

    private int lastPos = 0;

    public InternalProjectsListingMatrix(TrackedTimeSummary trackedTimeSum) {
        this.timesPerProject = trackedTimeSum.getProjectTimesMap();
        this.internalProjectMatrices = new ArrayList<>();
    }

    public void initializeMatrix(int startPos) {
        for (String project : timesPerProject.keySet()) {
            InternalProjectMatrix prjMatrix = new InternalProjectMatrix(project, timesPerProject.get(project));
            prjMatrix.initializeMatrix(startPos);
            this.internalProjectMatrices.add(prjMatrix);
            // rowsForReferencesPerProject.add(rowReference); no longer needed; is part of InternalProjectMatrix
            startPos = startPos+2;
        }
        lastPos = startPos;
    }

    public AbstractList<ProjectRowReference> getRowReferences() {
        return new ArrayList<>(); // TODO: remove that
    }
}
