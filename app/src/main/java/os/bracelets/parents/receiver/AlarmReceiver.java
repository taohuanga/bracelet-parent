package os.bracelets.parents.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;

import aio.health2world.utils.Logger;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.R;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String TAG = AlarmReceiver.class.getSimpleName();

    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认发音人
    private String voicer = "xiaoyan";

    String texts = "您有新的待办消息请及时查看";

    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

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

            //周期性提醒 下一个待办
//            Intent i = new Intent(AppConfig.ALARM_CLOCK);
//            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            PendingIntent sender = PendingIntent.getBroadcast(context, AppConfig.CLOCK_ID, i, PendingIntent.FLAG_CANCEL_CURRENT);
//            am.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, 100,
//                    sender);

            MyApplication.getInstance().speakVoice();
        }
    }

}
