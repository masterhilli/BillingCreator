package work.billing.Timesheet;

import org.jetbrains.annotations.NotNull;

import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mhillbrand on 2/21/2016.
 */
public class TrackedTimeSummary {
    private HashMap<String, TrackedTime> trackedTimesMap = new HashMap<>();
    private HashMap<String, List<String>> trackedTimesPerProject = new HashMap<>();
    private HashMap<String, List<String>> trackedTimePerTeamMember = new HashMap<>();

    //@NotNull
    public void addTrackedTime(TrackedTime trackedTime) throws TrackedTimeAlreadyExistsException {
        String newKey = createNewKey(trackedTime);
        if (trackedTimesMap.get(newKey) != null) {
            throw new TrackedTimeAlreadyExistsException(trackedTime);
        }
        trackedTimesMap.put(newKey, trackedTime);
        addForeignKeyToMap(trackedTimesPerProject, trackedTime.getProjectName(), newKey);
        addForeignKeyToMap(trackedTimePerTeamMember, trackedTime.getTeamMember(), newKey);
    }

    //@NotNull
    private void addForeignKeyToMap(HashMap<String, List<String>> mapToAddKey, String primaryKey, String foreignKey) {
        if (mapToAddKey.get(primaryKey.toLowerCase()) == null) {
            mapToAddKey.put(primaryKey.toLowerCase(), new ArrayList<>());
        }
        mapToAddKey.get(primaryKey.toLowerCase()).add(foreignKey);
    }

    public List<TrackedTime> receiveTrackedTimesPerTeamMember(String teamMember) {
        return receiveTrackedTimesFromMap(trackedTimePerTeamMember, teamMember.toLowerCase());
    }

    public List<TrackedTime> receiveTrackedTimesPerProject(String projectName) {
        return receiveTrackedTimesFromMap(trackedTimesPerProject, projectName.toLowerCase());
    }

    private List<TrackedTime> receiveTrackedTimesFromMap(HashMap<String, List<String>> stringListHashMap, String primaryKey) {
        if (stringListHashMap.get(primaryKey.toLowerCase()) == null)
            return null;
        List<TrackedTime> trackedTimes =
                stringListHashMap.get(primaryKey.toLowerCase()).stream().map(foreignKey -> trackedTimesMap.get(foreignKey)).
                        collect(Collectors.toList());
        return trackedTimes;
    }

    //@NotNull
    private String createNewKey(TrackedTime trackedTime) {
        String newKey = trackedTime.getProjectName()+trackedTime.getTeamMember()+trackedTime.getMonthTracked();
        return newKey.toLowerCase();
    }

    public void printTimesForAllTeamMembers() {
        System.out.println("************************************************");
        for (String primaryKey : trackedTimePerTeamMember.keySet()) {
            List<TrackedTime> trackedTimes = receiveTrackedTimesPerTeamMember(primaryKey);
            double sumOfTimes = 0.0;
            for (TrackedTime time : trackedTimes) {
                sumOfTimes += time.getHours();
            }
            System.out.printf("* %s = %.2f\n", trackedTimes.get(0).getTeamMember(), sumOfTimes);
        }
        System.out.println("************************************************");
    }

    public void printTimesForAllProjects() {
        System.out.println("************************************************");
        for (String primaryKey : trackedTimesPerProject.keySet()) {
            List<TrackedTime> trackedTimes = receiveTrackedTimesPerProject(primaryKey);
            if (trackedTimes != null) {
                double sumOfTimes = 0.0;
                System.out.printf("******* %s \n", trackedTimes.get(0).getProjectName());
                for (TrackedTime time : trackedTimes) {
                    sumOfTimes += time.getHours();
                    System.out.printf("* %s\n", time.toString());
                }
                System.out.printf("******* %s = %.2f\n",trackedTimes.get(0).getProjectName(), sumOfTimes);
            } else {
                System.out.printf("******* %s not time entries exist\n" + primaryKey);
            }
        }
        System.out.println("************************************************");
    }
}

