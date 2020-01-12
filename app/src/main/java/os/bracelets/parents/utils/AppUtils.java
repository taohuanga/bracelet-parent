package os.bracelets.parents.utils;

import android.content.Context;

import java.math.BigDecimal;

import os.bracelets.parents.R;

/**
 * Created by lishiyou on 2019/3/18.
 */

public class AppUtils {

    public static String getSex(Context context,int sex) {
        switch (sex) {
            case 0:
                return context.getString(R.string.unknow);
            case 1:
                return context.getString(R.string.man);
            case 2:
                return context.getString(R.string.woman);
        }
        return "";
    }

    public static String getDistance(int distance) {
        if (distance < 1000)
            return distance + "m";

        double f = (double) distance/1000;

        double d = new BigDecimal(f).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return d + "km";
    }
}
