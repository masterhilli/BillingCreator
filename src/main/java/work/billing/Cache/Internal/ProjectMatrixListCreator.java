package work.billing.Cache.Internal;

import work.billing.Timesheet.TrackedTime;
import work.billing.Timesheet.TrackedTimeSummary;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class ProjectMatrixListCreator {
    private final HashMap<String, List<TrackedTime>> timesPerProject;
    private List<ProjectMatrix> internalProjectMatrices;
    private ProjectSummary prjSummary;

    private int lastPosOfProjects = 0;
    private int firstPos = 0;

    public List<ProjectMatrix> getProjectMatrices() { return internalProjectMatrices;}
    public int getLastPosOfProjects() {
        return lastPosOfProjects;
    }
    public int getFirstPos() { return firstPos; }
    public ProjectSummary getPrjSummary() {return prjSummary;}

    public ProjectMatrixListCreator(TrackedTimeSummary trackedTimeSum, String projectLeadName, int teamSumHours) {
        prjSummary = new ProjectSummary(projectLeadName, teamSumHours);
        this.timesPerProject = trackedTimeSum.getProjectTimesMap();
        this.internalProjectMatrices = new ArrayList<>();
    }

    public void initialize(int startPos) {
        firstPos = startPos;
        for (String project : timesPerProject.keySet()) {
            ProjectMatrix prjMatrix = new ProjectMatrix(project, timesPerProject.get(project));
            prjMatrix.initializeMatrix(startPos);
            this.internalProjectMatrices.add(prjMatrix);
            startPos = prjMatrix.getSumRow()+3;
        }
        lastPosOfProjects = startPos;
        initializeSummary();
    }

    private void initializeSummary() {
        prjSummary.initialize(firstPos, lastPosOfProjects);
    }
}
