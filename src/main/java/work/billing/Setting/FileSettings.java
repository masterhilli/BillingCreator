package work.billing.Setting;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mhillbrand on 2/15/2016.
 * This is a POJO to read from a  JSON file
 */
public class FileSettings {
    public String exportFileId;
    public String projectLead;
    public List<String> searchParams;
    public HashMap<String, String> importFileId;
    public List<HourRate> personHourCosts;

    public HashMap<String, Integer> getHourRateAsHashMapPerTeamMember() {
        HashMap<String, Integer> hourRatePerTeamMember = new HashMap<>();
        for (HourRate rate : personHourCosts) {
            hourRatePerTeamMember.put(rate.name.toLowerCase(), rate.rate);
        }
        return hourRatePerTeamMember;
    }
}
