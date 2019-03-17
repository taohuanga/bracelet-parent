package os.bracelets.parents.utils;

import java.math.BigDecimal;

/**
 * Created by lishiyou on 2019/3/18.
 */

public class AppUtils {

    public static String getSex(int sex) {
        switch (sex) {
            case 0:
                return "未知";
            case 1:
                return "男";
            case 2:
                return "女";
        }
        return "";
    }

    public static String getDistance(int distance) {
        if (distance < 1000)
            return distance + "m";

        double f = (double) distance/1000;

        double d = new BigDecimal(f).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return d + "公里";
    }
}
