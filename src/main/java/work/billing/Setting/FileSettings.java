package work.billing.Setting;

import com.google.gdata.util.common.base.Pair;

import java.util.List;

/**
 * Created by mhillbrand on 2/15/2016.
 * This is a POJO to read from a  JSON file
 */
public class FileSettings {
    public String exportFileId;
    public List<String> importFileId;
    public List<HourRate> personHourCosts;
}
