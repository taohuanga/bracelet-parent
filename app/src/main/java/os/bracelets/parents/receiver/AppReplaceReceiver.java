package os.bracelets.parents.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import aio.health2world.utils.SPUtils;
import os.bracelets.parents.AppConfig;

public class AppReplaceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // 安装
        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {

        }
        // 覆盖安装
        if (action.equals(Intent.ACTION_PACKAGE_REPLACED)
                || action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            SPUtils.put(context, AppConfig.FIRST_IN, true);
        }
        // 移除
        if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {

        }
    }
}
