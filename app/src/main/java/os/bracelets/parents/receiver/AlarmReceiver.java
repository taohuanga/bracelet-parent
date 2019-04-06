package os.bracelets.parents.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppConfig.ALARM_CLOCK)) {
            //发送通知
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentTitle("待办提醒")
                    .setContentText("您有新的待办任务现在需要处理")
                    .setSmallIcon(R.mipmap.ic_app_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_app_logo))
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            manager.notify(1, notification);

//            //发送一条清空闹铃图标的广播
//            Intent intent1 = new Intent("com.g.android.NoColor");
//            intent1.putExtra("noteId", noteId);
//            context.sendBroadcast(intent1);
        }
    }
}
