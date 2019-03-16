package os.bracelets.parents;

import java.util.Locale;

/**
 * Created by lishiyou on 2019/3/16.
 */

public class CountryUtils {
    public CountryUtils() {
    }

    public static boolean getMonthAndDayFormate() {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        String contr = locale.getCountry();
        return lang != null && (lang.equals("zh") || lang.equals("ja") || lang.equals("ko") || lang.equals("en") && contr != null && contr.equals("US"));
    }

    public static boolean getLanguageFormate() {
        String language = Locale.getDefault().getLanguage();
        return language != null && language.equals("zh");
    }
}
