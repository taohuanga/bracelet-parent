package os.bracelets.parents.utils;

import android.content.Context;

import os.bracelets.parents.R;
import os.bracelets.parents.view.TitleBar;


/**
 * Created by Administrator on 2017/7/21 0021.
 */

public class TitleBarUtil {
    public static void setAttr(Context context, String leftText, String title, TitleBar titleBar) {
        titleBar.setTitle(title);
        titleBar.setTitleSize(16.5f);
        titleBar.setLeftText(leftText);
        titleBar.setLeftTextSize(16f);
        titleBar.setLeftImageResource(R.drawable.ic_chevron_left_black_36dp);
        titleBar.setLeftTextColor(context.getResources().getColor(R.color.white));
        titleBar.setTitleColor(context.getResources().getColor(R.color.white));
        titleBar.setActionTextColor(context.getResources().getColor(R.color.white));
    }
}
