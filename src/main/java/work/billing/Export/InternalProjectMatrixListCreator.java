package work.billing.Export;

import work.billing.I18N.I18N;
import work.billing.Spreadsheets.COL;
import work.billing.Spreadsheets.SpreadsheetFormulas;
import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class InternalProjectMatrixListCreator {
    private final HashMap<String, List<TrackedTime>> timesPerProject;
    private List<InternalProjectMatrix> internalProjectMatrices;
    private InternalProjectSummary prjSummary;

    private int lastPosOfProjects = 0;
    private int firstPos = 0;

    public List<? extends ProjectPositions> getInternalProjectMatrices() { return internalProjectMatrices;}
    public int getLastPosOfProjects() {
        return lastPosOfProjects;
    }
    public int getFirstPos() { return firstPos; }
    public InternalProjectSummary getPrjSummary() {return prjSummary;}

    public InternalProjectMatrixListCreator(TrackedTimeSummary trackedTimeSum, String projectLeadName) {
        prjSummary = new InternalProjectSummary(projectLeadName);
        this.timesPerProject = trackedTimeSum.getProjectTimesMap();
        this.internalProjectMatrices = new ArrayList<>();
    }

    public void initialize(int startPos) {
        firstPos = startPos;
        for (String project : timesPerProject.keySet()) {
            InternalProjectMatrix prjMatrix = new InternalProjectMatrix(project, timesPerProject.get(project));
            prjMatrix.initializeMatrix(startPos);
            this.internalProjectMatrices.add(prjMatrix);
            startPos = prjMatrix.getSumRow()+2;
        }
        lastPosOfProjects = startPos;
        initializeSummary();
    }

    private void initializeSummary() {
        prjSummary.initialize(firstPos, lastPosOfProjects);
    }
}
