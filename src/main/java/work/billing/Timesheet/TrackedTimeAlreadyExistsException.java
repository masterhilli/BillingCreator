package work.billing.Timesheet;

public class TrackedTimeAlreadyExistsException extends Exception {
    private final TrackedTime trackedTime;

    TrackedTimeAlreadyExistsException(TrackedTime trackedTime) {
        super(trackedTime.toString());
        this.trackedTime = trackedTime;
    }
}
