package work.billing.Setting;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by mhillbrand on 2/15/2016.
 */
public class TestFileSettingReader {
    private static String pathToFileSetting = "src\\test\\resources\\work.billing.Setting\\fileSettingsTest.json";
    private static String illegalPath = "test.xml";
    @Test
    public void testReadFileSettingsFromFileReturnsNull() {
        FileSettings actual = FileSettingReader.ReadFileSettingsFromFile(illegalPath    );
        Assert.assertNull(actual);
    }

    @Test
    public void testReadFileSettingsFromFileReturnstestFileAsObject() {
        FileSettings actual = FileSettingReader.ReadFileSettingsFromFile(pathToFileSetting);
        Assert.assertEquals(actual.exportFileId, "akjdsaoi-asdjfiaopo-12");
        Assert.assertEquals(actual.importFileId.get(0), "qwertzuiop1234567890-.,'*");
        Assert.assertEquals(actual.importFileId.get(1), "testtesttesttesttest---1234567989/*-+");
        //Assert.assertEquals(actual.personHourCosts.get(0).name, "Martin Hillbrand");
        //Assert.assertEquals(actual.personHourCosts.get(0).rate, 90);
    }
}
