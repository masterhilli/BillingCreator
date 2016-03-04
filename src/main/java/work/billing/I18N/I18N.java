package work.billing.I18N;

import java.util.ResourceBundle;

/**
 * Created by mhillbrand on 3/3/2016.
 */
public class I18N {
    public static final String EFFECTIVE_HOUR_RATE = getBundle().getString("EFFECTIVE_HOUR_RATE");
    private static ResourceBundle i18nBundle = null;
    public static ResourceBundle getBundle() {
        if (i18nBundle == null) {
            i18nBundle = ResourceBundle.getBundle("i18n/ger");
        }
        return i18nBundle;
    }

    public static final String HOUR_RATE    = getBundle().getString("HOUR_RATE");
    public static final String NET = getBundle().getString("NET");
    public static final String VAT = getBundle().getString("VAT");
    public static final String PRE_TAX       = getBundle().getString("PRE_TAX");
    public static final String TRAVEL_COSTS = getBundle().getString("TRAVEL_COSTS");
    public static final String SUM_AMOUNT   = getBundle().getString("SUM_AMOUNT");
    public static final String PERCENT20    = getBundle().getString("PERCENT20");
    public static final String ROUNDED      = getBundle().getString("ROUNDED");
    public static final String PRJ_LEAD_HOURS = getBundle().getString("PRJ_LEAD_HOURS");
    public static final String TEAM_HOURS = getBundle().getString("TEAM_HOURS");
    public static final String BILLING_SUM = getBundle().getString("BILLING_SUM");
    public static final String HOUR_SUM = getBundle().getString("HOUR_SUM");
    public static final String DESCRIPTION = getBundle().getString("DESCRIPTION");
    public static final String TAX_PERCENTAGE = getBundle().getString("TAX_PERCENTAGE");
}
