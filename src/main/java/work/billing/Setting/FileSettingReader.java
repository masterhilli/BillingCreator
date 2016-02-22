package work.billing.Setting;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by mhillbrand on 2/15/2016.
 */
public class FileSettingReader {

    public static FileSettings ReadFileSettingsFromFile(String path) {
        FileSettings fileSettings;
        String absoluteFilepath = Paths.get(path).toAbsolutePath().toString();
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.printf("--> %s", absoluteFilepath);
            fileSettings = mapper.readValue(new File(absoluteFilepath), FileSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return fileSettings;
    }
}
